package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.LongId;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TSerializer;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit test for {@link SignatureVerifierWithIdentityStoreAndNotifier}
 */
public class SignatureVerifierWithIdentityStoreAndNotifierTest extends Signer
{
	private final Notifier notifierBackend=new Notifier();
	private final ClientId clientId=new ClientId(0,0);
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
		setupClient();
		final LongId payload0=new LongId(deviceToken0);
		final LongId payload1=new LongId(deviceToken0+1);
		final Map<Long,LongId> expected=new HashMap<>(deviceTokens.size()+1);
		expected.put(deviceToken0,payload0);
		for(final Long l : deviceTokens)
		{
			expected.put(l,payload1);
		}
		final TestSignatureVerifierWithIdentityStoreAndNotifier testSignatureVerifierWithIdentityStoreAndNotifier=new
			                                                                                                          TestSignatureVerifierWithIdentityStoreAndNotifier(userDataStore,DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration()),clientNotifier);
		testSignatureVerifierWithIdentityStoreAndNotifier.notifyClient(deviceToken0,payload0);
		testSignatureVerifierWithIdentityStoreAndNotifier.notifyClients(deviceTokens,payload1);
		for(final Long deviceToken : expected.keySet())
		{
			final Long notificationId=notifierBackend.sentNotifications.get(deviceToken);
			assertNotNull(notificationId);
			final LongId notificationLongId=new LongId(notificationId);
			final LongId retrieved=new LongId();
			final Future<LongId> future=new DefaultFutureResult<>();
			testSignatureVerifierWithIdentityStoreAndNotifier.forward(retrieved,notificationLongId,sign(new TSerializer
				                                                                                            ().serialize
					                                                                                               (notificationLongId),
				                                                                                           clientId),future);
			assertTrue(future.succeeded());
			assertEquals(expected.get(deviceToken),retrieved);
			testSignatureVerifierWithIdentityStoreAndNotifier.forward(retrieved,notificationLongId,sign(new TSerializer
				                                                                                            ().serialize
					                                                                                               (notificationLongId),
				                                                                                           clientId),future);
			assertTrue(future.failed());
		}
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
		user.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		user.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
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
		client.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
		final HashMap<CharSequence,Client> clients=new HashMap<>(1);
		clients.put(TypeConverter.convert(clientId.getClient()),client);
		user.setClients(clients);
		userDataStore.put(clientId.getUser(),user);
	}

	class TestSignatureVerifierWithIdentityStoreAndNotifier extends SignatureVerifierWithIdentityStoreAndNotifier
	{
		/**
		 * Constructor from a datastore
		 *
		 * @param userDataStore     The user datastore
		 * @param identityDatastore The identity datastore
		 * @param clientNotifier    The client notifier interface
		 */
		TestSignatureVerifierWithIdentityStoreAndNotifier(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
		{
			super(userDataStore,identityDatastore,clientNotifier);
		}
	}
}