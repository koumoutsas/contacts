package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.server.gora.EncryptionAlgorithm;
import com.kareebo.contacts.server.gora.EncryptionKey;
import com.kareebo.contacts.server.gora.PublicKeys;
import com.kareebo.contacts.server.gora.UserAgent;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.client.jobs.Notification;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link SendContactCard}
 */
public class SendContactCardTest
{
	@Test
	/**
	 * Create a user 0, two clients (0,0) and (0,1), user 1, two clients (1,0) and (1,1), user 0 does a request for user 1, and clients (1,0)
	 * and (1,1) get notifications with the two encryption keys for clients (0,0) and (0,1). For the error case, set the algorithm of the verification
	 * key for client (0,1) to invalid and run the same flow and check that no notifications were issued.
	 */
	public void testSendContactCard1() throws Exception
	{
		abstract class Base1 extends Base
		{
			protected final Future<Void> future=new DefaultFutureResult<>();
			final ClientId clientId10=new ClientId(1,0);
			final ClientId clientId11=new ClientId(1,1);
			final ClientId clientId01=new ClientId(0,1);
			final com.kareebo.contacts.thrift.EncryptionKey e0;
			final com.kareebo.contacts.thrift.EncryptionKey e1;
			private final com.kareebo.contacts.thrift.HashBuffer u=new com.kareebo.contacts.thrift.HashBuffer();

			Base1() throws GoraException, NoSuchAlgorithmException
			{
				final ByteBuffer b0=ByteBuffer.wrap("0".getBytes());
				b0.mark();
				e0=new com.kareebo.contacts.thrift.EncryptionKey(b0,com.kareebo.contacts.thrift.EncryptionAlgorithm.RSA2048);
				final User user0=userDatastore.get(clientId.getUser());
				final Map<CharSequence,Client> clients0=user0.getClients();
				clients0.values().iterator().next().getKeys().setEncryption(TypeConverter.convert(e0));
				final ByteBuffer b1=ByteBuffer.wrap("1".getBytes());
				b1.mark();
				e1=new com.kareebo.contacts.thrift.EncryptionKey(b1,com.kareebo.contacts.thrift.EncryptionAlgorithm.RSA2048);
				final Client client01=createClient(clientId01,verificationKey);
				client01.getKeys().setEncryption(TypeConverter.convert(e1));
				clients0.put(TypeConverter.convert(clientId01.getClient()),client01);
				user0.setClients(clients0);
				final Client client10=createClient(clientId10,verificationKey);
				final Client client11=createClient(clientId11,verificationKey);
				final User user1=new User();
				user1.setBlind(b0);
				final Map<CharSequence,Client> clients1=new HashMap<>(2);
				clients1.put(TypeConverter.convert(clientId10.getClient()),client10);
				clients1.put(TypeConverter.convert(clientId11.getClient()),client11);
				user1.setClients(clients1);
				user1.setIdentities(new ArrayList<>());
				user1.setSentRequests(new ArrayList<>());
				user1.setId(clientId10.getUser());
				userDatastore.put(clientId.getUser(),user0);
				userDatastore.put(clientId10.getUser(),user1);
				final HashIdentity hashIdentity=new HashIdentity();
				hashIdentity.setHash(b0);
				final HashIdentityValue hashIdentityValue=new HashIdentityValue();
				hashIdentityValue.setConfirmers(new ArrayList<>());
				hashIdentityValue.setId(clientId10.getUser());
				hashIdentity.setHashIdentity(hashIdentityValue);
				u.setAlgorithm(HashAlgorithm.SHA256);
				u.setBuffer(b0);
				identityDataStore.put(b0,hashIdentity);
			}

			@SuppressWarnings("WeakerAccess")
			void run() throws TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				sendContactCard.sendContactCard1(u,sign(u,clientId),future);
				check();
			}

			abstract void check() throws TException;
		}
		new Base1()
		{
			@Override
			void check() throws TException
			{
				assertTrue(future.succeeded());
				assertEquals(2,notifier.size());
				final Notification notification0=notifier.get(clientId10.getClient());
				assertEquals(com.kareebo.contacts.client.processor.SendContactCard.method1,notification0.getMethod());
				checkInternal(notification0.getId());
				final Notification notification1=notifier.get(clientId11.getClient());
				assertEquals(com.kareebo.contacts.client.processor.SendContactCard.method1,notification1.getMethod());
				checkInternal(notification1.getId());
			}

			private void checkInternal(final long notificationId) throws FailedOperation
			{
				final EncryptionKeys encryptionKeys=new EncryptionKeys();
				clientNotifier.get(encryptionKeys,notificationId);
				assertEquals(clientId.getUser(),encryptionKeys.getUserId());
				final Map<Long,com.kareebo.contacts.thrift.EncryptionKey> keys=encryptionKeys.getKeys();
				assertEquals(2,keys.size());
				final com.kareebo.contacts.thrift.EncryptionKey e0=keys.get(clientId.getClient());
				assertNotNull(e0);
				assertEquals(this.e0,e0);
				final com.kareebo.contacts.thrift.EncryptionKey e1=keys.get(clientId01.getClient());
				assertNotNull(e1);
				assertEquals(this.e1,e1);
			}
		}.run();
		new Base1()
		{
			{
				userDatastore.get(clientId.getUser()).getClients().values().iterator().next().getKeys().getEncryption()
					.setAlgorithm(EncryptionAlgorithm.Fake);
				sendContactCard=new SendContactCardError(userDatastore,identityDataStore,clientNotifier);
			}

			@Override
			void check()
			{
				assertTrue(future.failed());
				assertEquals(0,notifier.size());
			}
		}.run();
	}

	@Test
	public void testSendContactCard2() throws Exception
	{
		new BaseForwarding<EncryptionKeys>(com.kareebo.contacts.client.protocol.SendContactCard.method1)
		{
			@Override
			TBase construct()
			{
				return new EncryptionKeys(0,new HashMap<>());
			}

			@Override
			void call(final LongId notificationId,final SignatureBuffer signatureBuffer,final Future<EncryptionKeys> future)
			{
				sendContactCard.sendContactCard2(notificationId,signatureBuffer,future);
			}
		}.run();
	}

	@Test
	/**
	 * Create a user (1), create two clients (0,1) for it, sign an encrypted buffer for the two clients with the keys for client (0,0), send
	 * the buffers and check that the notifications are sent out. For the error case, set the algorithm of the verification key for client (0,
	 * 0) to invalid and run the same flow.
	 */
	public void testSendContactCard3() throws Exception
	{
		abstract class Base3 extends Base
		{
			protected final Future<Void> future=new DefaultFutureResult<>();
			final EncryptedBufferSignedWithVerificationKey e0;
			final EncryptedBufferSignedWithVerificationKey e1;
			private final Set<EncryptedBufferSigned> encryptedBufferSignedSet=new HashSet<>(2);
			private final Long userId=clientId.getUser()+1;
			final ClientId clientId0=new ClientId(userId,0);
			final ClientId clientId1=new ClientId(userId,1);

			Base3() throws GoraException, TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				final User user=new User();
				user.setId(userId);
				final ByteBuffer b=ByteBuffer.wrap("".getBytes());
				b.mark();
				user.setBlind(b);
				user.setIdentities(new ArrayList<>());
				user.setSentRequests(new ArrayList<>());
				final Map<CharSequence,Client> clients=new HashMap<>(2);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(com.kareebo.contacts.server.gora.SignatureAlgorithm.Fake);
				verificationKey.setBuffer(b);
				clients.put(TypeConverter.convert(clientId0.getClient()),createClient(clientId0,verificationKey));
				clients.put(TypeConverter.convert(clientId1.getClient()),createClient(clientId1,verificationKey));
				user.setClients(clients);
				userDatastore.put(userId,user);
				final com.kareebo.contacts.thrift.EncryptedBuffer encryptedBuffer0=new com.kareebo.contacts.thrift.EncryptedBuffer
					                                                                   (b,com.kareebo.contacts.thrift.EncryptionAlgorithm.Fake,
						                                                                   clientId0);
				final EncryptedBufferSigned encryptedBufferSigned0=new EncryptedBufferSigned(encryptedBuffer0,sign(encryptedBuffer0,clientId));
				encryptedBufferSignedSet.add(encryptedBufferSigned0);
				e0=new EncryptedBufferSignedWithVerificationKey(encryptedBufferSigned0,TypeConverter.convert(this.verificationKey));
				final com.kareebo.contacts.thrift.EncryptedBuffer encryptedBuffer1=new com.kareebo.contacts.thrift.EncryptedBuffer
					                                                                   (b,com.kareebo.contacts.thrift.EncryptionAlgorithm.Fake,
						                                                                   clientId1);
				final EncryptedBufferSigned encryptedBufferSigned1=new EncryptedBufferSigned(encryptedBuffer1,sign(encryptedBuffer1,clientId));
				encryptedBufferSignedSet.add(encryptedBufferSigned1);
				e1=new EncryptedBufferSignedWithVerificationKey(encryptedBufferSigned1,TypeConverter.convert(this.verificationKey));
			}

			@SuppressWarnings("WeakerAccess")
			void run() throws TException
			{
				sendContactCard.sendContactCard3(encryptedBufferSignedSet,future);
				check();
			}

			abstract void check() throws TException;
		}
		new Base3()
		{
			@Override
			void check() throws TException
			{
				assertTrue(future.succeeded());
				assertEquals(2,notifier.size());
				final EncryptedBufferSignedWithVerificationKey e0=new EncryptedBufferSignedWithVerificationKey();
				final Notification notification0=notifier.get(clientId0.getClient());
				assertEquals(com.kareebo.contacts.client.protocol.SendContactCard.method3,notification0.getMethod());
				clientNotifier.get(e0,notification0.getId());
				assertEquals(this.e0,e0);
				final EncryptedBufferSignedWithVerificationKey e1=new EncryptedBufferSignedWithVerificationKey();
				final Notification notification1=notifier.get(clientId1.getClient());
				assertEquals(com.kareebo.contacts.client.protocol.SendContactCard.method3,notification1.getMethod());
				clientNotifier.get(e1,notification1.getId());
				assertEquals(this.e1,e1);
			}
		}.run();
		new Base3()
		{
			{
				verificationKey.setAlgorithm(com.kareebo.contacts.server.gora.SignatureAlgorithm.Fake);
				sendContactCard=new SendContactCardError(userDatastore,null,clientNotifier);
			}

			@Override
			void check() throws FailedOperation
			{
				assertTrue(future.failed());
				assertEquals(0,notifier.size());
			}
		}.run();
	}

	@Test
	public void testSendContactCard4() throws Exception
	{
		new BaseForwarding<EncryptedBufferSignedWithVerificationKey>(com.kareebo.contacts.client.protocol.SendContactCard.method3)
		{
			@Override
			void call(final LongId notificationId,final SignatureBuffer signatureBuffer,final Future<EncryptedBufferSignedWithVerificationKey> future)
			{
				sendContactCard.sendContactCard4(notificationId,signatureBuffer,future);
			}

			@Override
			TBase construct() throws NoSuchAlgorithmException
			{
				final ByteBuffer b=ByteBuffer.wrap("".getBytes());
				b.mark();
				return new EncryptedBufferSignedWithVerificationKey(new EncryptedBufferSigned(new com.kareebo.contacts.thrift
					                                                                                  .EncryptedBuffer(b,com.kareebo.contacts.thrift.EncryptionAlgorithm.Fake,clientId),new SignatureBuffer(b,SignatureAlgorithm.SHA512withECDSAprime239v1,clientId)),TypeConverter.convert(verificationKey));
			}
		}.run();
	}

	private class SendContactCardError extends SendContactCard
	{
		SendContactCardError(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
		{
			super(userDataStore,identityDatastore,clientNotifier);
		}

		@Override
		void verify(@Nonnull final TBase plaintext,@Nonnull final SignatureBuffer signature,@Nonnull final Reply<?> reply,@Nonnull final After after)
		{
			final Client client;
			try
			{
				client=clientDBAccessor.get(signature.getClient());
			}
			catch(FailedOperation failedOperation)
			{
				reply.setFailure(failedOperation);
				return;
			}
			try
			{
				after.run(clientDBAccessor.user,client);
			}
			catch(FailedOperation failedOperation)
			{
				reply.setFailure(failedOperation);
				return;
			}
			clientDBAccessor.close();
			reply.setReply();
		}
	}

	private abstract class BaseForwarding<T extends TBase> extends Base
	{
		final private ServiceMethod method;

		BaseForwarding(final ServiceMethod method) throws GoraException
		{
			this.method=method;
		}

		void run() throws TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
			                  SignatureException
		{
			final TBase o=construct();
			final long deviceToken=99;
			final long notificationId=999;
			notifier.put(deviceToken,new Notification(method,notificationId));
			final PendingNotification pendingNotification=new PendingNotification();
			pendingNotification.setId(notificationId);
			final ByteBuffer b=ByteBuffer.wrap(new TSerializer().serialize(o));
			b.mark();
			pendingNotification.setPayload(b);
			pendingNotificationDataStore.put(notificationId,pendingNotification);
			final LongId notificationLongId=new LongId(notificationId);
			final Future<T> future=new DefaultFutureResult<>();
			call(notificationLongId,sign(notificationLongId,clientId),future);
			assertTrue(future.succeeded());
			assertEquals(o,future.result());
		}

		abstract TBase construct() throws NoSuchAlgorithmException;

		abstract void call(final LongId notificationId,final SignatureBuffer signatureBuffer,final Future<T> future);
	}

	private class Base extends Signer
	{
		final protected ClientId clientId=new ClientId(0,0);
		final Notifier notifier;
		final ClientNotifier clientNotifier;
		final DataStore<Long,PendingNotification> pendingNotificationDataStore;
		final DataStore<ByteBuffer,HashIdentity> identityDataStore;
		final DataStore<Long,User> userDatastore;
		SendContactCard sendContactCard;

		Base() throws GoraException
		{
			userDatastore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
			notifier=new Notifier();
			pendingNotificationDataStore=DataStoreFactory.getDataStore(Long.class,PendingNotification.class,new Configuration());
			clientNotifier=new
				               ClientNotifier(notifier,pendingNotificationDataStore);
			identityDataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new
				                                                                                    Configuration());
			sendContactCard=new SendContactCard(userDatastore,identityDataStore,clientNotifier);
			final User user=new User();
			final ByteBuffer b=ByteBuffer.wrap("".getBytes());
			b.mark();
			user.setBlind(b);
			user.setId(clientId.getUser());
			user.setIdentities(new ArrayList<>());
			user.setSentRequests(new ArrayList<>());
			final Map<CharSequence,Client> clients=new HashMap<>(1);
			clients.put(TypeConverter.convert(clientId.getClient()),createClient(clientId,verificationKey));
			user.setClients(clients);
			userDatastore.put(user.getId(),user);
		}

		Client createClient(final ClientId clientId,final VerificationKey verificationKey)
		{
			final Client client=new Client();
			client.setComparisonIdentities(new ArrayList<>());
			client.setDeviceToken(clientId.getClient());
			final PublicKeys publicKeys=new PublicKeys();
			publicKeys.setVerification(verificationKey);
			final EncryptionKey encryptionKey=new EncryptionKey();
			encryptionKey.setAlgorithm(EncryptionAlgorithm.RSA2048);
			final ByteBuffer b=ByteBuffer.wrap("".getBytes());
			b.mark();
			encryptionKey.setBuffer(b);
			publicKeys.setEncryption(encryptionKey);
			client.setKeys(publicKeys);
			final UserAgent userAgent=new UserAgent();
			userAgent.setPlatform("");
			userAgent.setVersion("");
			client.setUserAgent(userAgent);
			return client;
		}
	}
}