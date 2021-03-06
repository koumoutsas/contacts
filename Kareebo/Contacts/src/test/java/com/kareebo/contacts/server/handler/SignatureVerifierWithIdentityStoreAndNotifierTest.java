package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.Notification;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link SignatureVerifierWithIdentityStoreAndNotifier}
 */
public class SignatureVerifierWithIdentityStoreAndNotifierTest extends Signer
{
	private final Notifier notifierBackend=new Notifier();
	private final ClientId clientId=new ClientId(0,0);
	private final ServiceMethod method=new ServiceMethod("a","b");
	private DataStore<Long,User> userDataStore;
	private ClientNotifier clientNotifier;

	@Test
	/**
	 * Create a client (0,0), create three notifications for it, send them, and check if they arrived
	 */
	public void test() throws Exception
	{
		final long deviceToken0=0;
		final List<Long> deviceTokens=Arrays.asList(deviceToken0+1,deviceToken0+2);
		final List<Long> deviceTokens2=Arrays.asList(deviceToken0+3,deviceToken0+4);
		setupClient();
		final LongId payload0=new LongId(deviceToken0);
		final LongId payload1=new LongId(deviceToken0+1);
		final LongId payload2=new LongId(deviceToken0+2);
		final Map<Long,LongId> expected=new HashMap<>(deviceTokens.size()+deviceTokens2.size()+1);
		expected.put(deviceToken0,payload0);
		deviceTokens.forEach(l->expected.put(l,payload1));
		deviceTokens2.forEach(l->expected.put(l,payload2));
		final Map<Long,NotificationObject> notifications=deviceTokens2.stream().collect(Collectors.toMap(Function.identity(),l->new
			                                                                                                                        NotificationObject(method,payload2)));
		final TestSignatureVerifierWithIdentityStoreAndNotifier testSignatureVerifierWithIdentityStoreAndNotifier=new
			                                                                                                          TestSignatureVerifierWithIdentityStoreAndNotifier(userDataStore,DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration()),clientNotifier);
		testSignatureVerifierWithIdentityStoreAndNotifier.notifyClient(deviceToken0,new NotificationObject(method,payload0));
		testSignatureVerifierWithIdentityStoreAndNotifier.notifyClients(deviceTokens,new NotificationObject(method,payload1));
		testSignatureVerifierWithIdentityStoreAndNotifier.notifyClients(notifications);
		for(final Long deviceToken : expected.keySet())
		{
			final Notification notification=notifierBackend.get(deviceToken);
			assertEquals(method,notification.getMethod());
			final Long notificationId=notification.getId();
			final LongId notificationLongId=new LongId(notificationId);
			final LongId retrieved=new LongId();
			final Future<LongId> future=new DefaultFutureResult<>();
			testSignatureVerifierWithIdentityStoreAndNotifier.forward(retrieved,notificationLongId,sign(notificationLongId,
				clientId),future);
			assertTrue(future.succeeded());
			assertEquals(expected.get(deviceToken),retrieved);
			testSignatureVerifierWithIdentityStoreAndNotifier.forward(retrieved,notificationLongId,sign(notificationLongId,
				clientId),future);
			assertTrue(future.failed());
		}
		testSignatureVerifierWithIdentityStoreAndNotifier.notifyClient(deviceToken0,new NotificationObject(method,payload0));
		final LongId retrieved=new LongId();
		testSignatureVerifierWithIdentityStoreAndNotifier.get(retrieved,notifierBackend.get(deviceToken0).getId());
		assertEquals(new LongId(deviceToken0),retrieved);
	}

	private void setupClient() throws Exception
	{
		userDataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		clientNotifier=new ClientNotifier(notifierBackend,DataStoreFactory.getDataStore(Long.class,PendingNotification.class,new Configuration()));
		final User user=new User();
		final byte[] buffer={'a','b'};
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		user.setBlind(byteBuffer);
		user.setIdentities(new ArrayList<>());
		user.setSentRequests(new ArrayList<>());
		final UserAgent userAgent=new UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		final PublicKeys publicKeys=new PublicKeys();
		final EncryptionKey encryptionKey=new EncryptionKey();
		encryptionKey.setAlgorithm(EncryptionAlgorithm.RSA2048);
		encryptionKey.setBuffer(byteBuffer);
		publicKeys.setEncryption(encryptionKey);
		publicKeys.setVerification(verificationKey);
		final Client client=new Client();
		client.setUserAgent(userAgent);
		client.setKeys(publicKeys);
		client.setComparisonIdentities(new ArrayList<>());
		final HashMap<CharSequence,Client> clients=new HashMap<>(1);
		clients.put(TypeConverter.convert(clientId.getClient()),client);
		user.setClients(clients);
		userDataStore.put(clientId.getUser(),user);
	}

	private class TestSignatureVerifierWithIdentityStoreAndNotifier extends SignatureVerifierWithIdentityStoreAndNotifier
	{
		TestSignatureVerifierWithIdentityStoreAndNotifier(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
		{
			super(userDataStore,identityDatastore,clientNotifier);
		}
	}
}