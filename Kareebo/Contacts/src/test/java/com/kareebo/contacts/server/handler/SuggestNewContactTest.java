package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.server.gora.HashAlgorithm;
import com.kareebo.contacts.server.gora.HashBuffer;
import com.kareebo.contacts.server.gora.PublicKeys;
import com.kareebo.contacts.server.gora.UserAgent;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.EncryptedBuffer;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.VerificationKey;
import com.kareebo.contacts.thrift.client.jobs.Notification;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TException;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link SuggestNewContact}
 */
public class SuggestNewContactTest
{
	@Test
	/**
	 * Add a {@link com.kareebo.contacts.thrift.HashBuffer} uB to the pending notifications for client (0,0), create clients (1,0) and (1,1),
	 * map uB to user 1, call suggestNewContact1 and retrieve the encryption keys for (1,0) and (1,1), and uB
	 */
	public void testSuggestNewContact1() throws Exception
	{
		new BaseFull()
		{
			protected void runImpl() throws TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final com.kareebo.contacts.thrift.HashBuffer uB=new com.kareebo.contacts.thrift.HashBuffer(buffer,com.kareebo
					                                                                                                  .contacts.thrift.HashAlgorithm.SHA256);
				clientNotifier.put(deviceToken,new NotificationObject(new ServiceMethod("",""),uB));
				final LongId notificationId=new LongId(notifierBackend.getFirst().getId());
				final HashIdentity identity=new HashIdentity();
				identity.setHash(buffer);
				final HashIdentityValue hashIdentity=new HashIdentityValue();
				hashIdentity.setConfirmers(new ArrayList<>());
				hashIdentity.setId(clientId0.getUser());
				identity.setHashIdentity(hashIdentity);
				identityDataStore.put(buffer,identity);
				final Future<EncryptionKeysWithHashBuffer> future=new DefaultFutureResult<>();
				suggestNewContact.suggestNewContact1(notificationId,sign(notificationId,clientId),future);
				assertTrue(future.succeeded());
				final Map<Long,EncryptionKey> encryptionKeyMap=new HashMap<>(2);
				final Long user1=clientId0.getUser();
				final Map<CharSequence,Client> clients=userDataStore.get(user1).getClients();
				encryptionKeyMap.put(clientId0.getClient(),TypeConverter.convert(clients.get(TypeConverter.convert(clientId0.getClient())).getKeys()
					                                                                 .getEncryption()));
				encryptionKeyMap.put(clientId1.getClient(),TypeConverter.convert(clients.get(TypeConverter.convert(clientId1.getClient())).getKeys()
					                                                                 .getEncryption()));
				assertEquals(new EncryptionKeysWithHashBuffer(new EncryptionKeys(user1,encryptionKeyMap),uB),future.result());
			}
		}.run();
	}

	@Test
	/**
	 * Add a {@link com.kareebo.contacts.thrift.HashBuffer} uB to the pending notifications for client (0,0), create client (1,0),
	 * corrupt the algorithm for client(1,0), map uB to user 1, call suggestNewContact1 and test it failed
	 */
	public void testSuggestNewContact1InvalidAlgorithm() throws Exception
	{
		new BaseFull()
		{
			protected void runImpl() throws TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final com.kareebo.contacts.thrift.HashBuffer uB=new com.kareebo.contacts.thrift.HashBuffer(buffer,com.kareebo
					                                                                                                  .contacts.thrift.HashAlgorithm.SHA256);
				clientNotifier.put(deviceToken,new NotificationObject(new ServiceMethod("",""),uB));
				final LongId notificationId=new LongId(notifierBackend.getFirst().getId());
				final HashIdentity identity=new HashIdentity();
				identity.setHash(buffer);
				final HashIdentityValue hashIdentity=new HashIdentityValue();
				hashIdentity.setConfirmers(new ArrayList<>());
				hashIdentity.setId(clientId0.getUser());
				identity.setHashIdentity(hashIdentity);
				identityDataStore.put(buffer,identity);
				userDataStore.get(clientId0.getUser()).getClients().get(TypeConverter.convert(clientId0.getClient())).getKeys()
					.getEncryption().setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.Fake);
				final Future<EncryptionKeysWithHashBuffer> future=new DefaultFutureResult<>();
				suggestNewContact.suggestNewContact1(notificationId,sign(notificationId,clientId),future);
				assertTrue(future.failed());
			}
		}.run();
	}

	@Test
	/**
	 * Create a {@link HashBuffer} uB, add it to the sent requests of user 0, call suggestNewContact2 with uB and check that the call failed
	 */
	public void testSuggestNewContact2AlreadySent() throws Exception
	{
		new Base()
		{
			void run() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException, TException
			{
				final HashBuffer uA=new HashBuffer();
				uA.setAlgorithm(HashAlgorithm.SHA256);
				final ByteBuffer bufferA=ByteBuffer.wrap("a".getBytes());
				bufferA.mark();
				uA.setBuffer(bufferA);
				final HashBuffer uB=new HashBuffer();
				uB.setAlgorithm(HashAlgorithm.SHA256);
				final ByteBuffer bufferB=ByteBuffer.wrap("b".getBytes());
				bufferB.mark();
				uB.setBuffer(bufferB);
				final User user=userDataStore.get(clientId.getUser());
				final List<HashBuffer> sentRequests=user.getSentRequests();
				sentRequests.add(uA);
				sentRequests.add(uB);
				user.setSentRequests(sentRequests);
				userDataStore.close();
				final Future<Void> future=new DefaultFutureResult<>();
				final com.kareebo.contacts.thrift.HashBuffer uBConverted=TypeConverter.convert(uB);
				//noinspection ConstantConditions
				suggestNewContact.suggestNewContact2(null,uBConverted,sign(uBConverted,clientId),future);
				assertTrue(future.succeeded());
				uBConverted.setAlgorithm(com.kareebo.contacts.thrift.HashAlgorithm.Fake);
				//noinspection ConstantConditions
				suggestNewContact.suggestNewContact2(null,uBConverted,sign(uBConverted,clientId),future);
				assertTrue(future.failed());
			}
		}.run();
	}

	@Test
	/**
	 * Call suggestNewContact2 with a bad signature for uB
	 */
	public void testSuggestNewContact2EncryptedBufferFailedSignature() throws Exception
	{
		new Base()
		{
			void run() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException, TException
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final com.kareebo.contacts.thrift.HashBuffer uB=new com.kareebo.contacts.thrift.HashBuffer(buffer,com.kareebo.contacts
					                                                                                                  .thrift.HashAlgorithm.SHA256);
				final Future<Void> future=new DefaultFutureResult<>();
				suggestNewContact.suggestNewContact2(new HashSet<>(Collections.singletonList(new EncryptedBufferSigned(new EncryptedBuffer
					                                                                                                       (buffer,
						                                                                                                       EncryptionAlgorithm.RSA2048,clientId),
					                                                                                                      new
						                                                                                                      SignatureBuffer(buffer,SignatureAlgorithm.SHA256withECDSAprime239v1,clientId)))),uB,
					sign(uB,clientId),future);
				assertTrue(future.failed());
				assertEquals(1,userDataStore.get(clientId.getUser()).getSentRequests().size());
			}
		}.run();
	}

	@Test
	/**
	 * Create clients (1,0) and (1,1), create a {@link HashBuffer} uB, create a set of 2 {@link EncryptedBuffer}s, one for each of the clients
	 * for user 1, call suggestNewContact2 with the set and uB and check that two notifications are generated for the clients (1,0) and (1,1)
	 */
	public void testSuggestNewContact2() throws Exception
	{
		new BaseFull()
		{
			protected void runImpl() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException, TException,
				                                InvalidAlgorithmParameterException
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final com.kareebo.contacts.thrift.HashBuffer uB=new com.kareebo.contacts.thrift.HashBuffer(buffer,com.kareebo.contacts
					                                                                                                  .thrift.HashAlgorithm.SHA256);
				final Future<Void> future=new DefaultFutureResult<>();
				final EncryptedBuffer encryptedBuffer0=new EncryptedBuffer(buffer,EncryptionAlgorithm.RSA2048,clientId0);
				final EncryptedBufferSigned encryptedBufferSigned0=new EncryptedBufferSigned(encryptedBuffer0,sign
					                                                                                              (encryptedBuffer0,clientId
					                                                                                              ));
				final EncryptedBuffer encryptedBuffer1=new EncryptedBuffer(buffer,EncryptionAlgorithm.RSA2048,clientId1);
				final EncryptedBufferSigned encryptedBufferSigned1=new EncryptedBufferSigned(encryptedBuffer1,sign
					                                                                                              (encryptedBuffer1,clientId));
				suggestNewContact.suggestNewContact2(new HashSet<>(Arrays.asList(encryptedBufferSigned0,encryptedBufferSigned1)),
					uB,sign(uB,clientId),future);
				assertTrue(future.succeeded());
				assertEquals(1,userDataStore.get(clientId.getUser()).getSentRequests().size());
				assertEquals(2,notifierBackend.size());
				final Notification notification0=notifierBackend.get(deviceToken0);
				assertEquals(com.kareebo.contacts.client.protocol.SuggestNewContact.method2,notification0.getMethod());
				final EncryptedBufferSignedWithVerificationKey retrieved0=new EncryptedBufferSignedWithVerificationKey();
				clientNotifier.get(retrieved0,notifierBackend.get(deviceToken0).getId());
				final VerificationKey verificationKeyConverted=TypeConverter.convert(verificationKey);
				assertEquals(new EncryptedBufferSignedWithVerificationKey(encryptedBufferSigned0,verificationKeyConverted),retrieved0);
				final EncryptedBufferSignedWithVerificationKey retrieved1=new EncryptedBufferSignedWithVerificationKey();
				final Notification notification1=notifierBackend.get(deviceToken1);
				assertEquals(com.kareebo.contacts.client.protocol.SuggestNewContact.method2,notification1.getMethod());
				clientNotifier.get(retrieved1,notification1.getId());
				assertEquals(new EncryptedBufferSignedWithVerificationKey(encryptedBufferSigned1,verificationKeyConverted),retrieved1);
			}
		}.run();
	}

	@Test
	/**
	 * Put a notification for an {@link EncryptedBufferSignedWithVerificationKey}, call suggestNewContact3 and check that it is returned in the
	 * result
	 */
	public void testSuggestNewContact3() throws Exception
	{
		new Base()
		{
			void run() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException, TException
			{
				final byte[] bytes="a".getBytes();
				final ByteBuffer buffer=ByteBuffer.wrap(bytes);
				buffer.mark();
				final EncryptedBufferSignedWithVerificationKey expected=new EncryptedBufferSignedWithVerificationKey(new
					                                                                                                     EncryptedBufferSigned(new EncryptedBuffer(buffer,EncryptionAlgorithm.RSA2048,clientId),sign(bytes,clientId)),
					                                                                                                    TypeConverter.convert(verificationKey));
				clientNotifier.put(deviceToken,new NotificationObject(com.kareebo.contacts.client.protocol.SuggestNewContact.method2,expected));
				final Future<EncryptedBufferSignedWithVerificationKey> future=new DefaultFutureResult<>();
				final LongId id=new LongId(notifierBackend.getFirst().getId());
				suggestNewContact.suggestNewContact3(id,sign(id,clientId),future);
				assertTrue(future.succeeded());
				assertEquals(expected,future.result());
			}
		}.run();
	}

	private abstract class BaseFull extends Base
	{
		final Long deviceToken0=deviceToken+1;
		final Long deviceToken1=deviceToken+2;
		ClientId clientId0;
		ClientId clientId1;

		BaseFull() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, GoraException
		{
		}

		final void run() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, TException, SignatureException
		{
			setupOtherUser();
			runImpl();
		}

		private void setupOtherUser() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException,
			                                     InvalidKeyException
		{
			final User user=new User();
			final ByteBuffer b=ByteBuffer.wrap("".getBytes());
			b.mark();
			user.setBlind(b);
			final long userId1=clientId.getUser()+1;
			clientId0=new ClientId(userId1,0);
			clientId1=new ClientId(userId1,1);
			user.setId(userId1);
			final Map<CharSequence,Client> clients=new HashMap<>(2);
			final Client client0=new Client();
			client0.setComparisonIdentities(new ArrayList<>());
			client0.setDeviceToken(deviceToken0);
			final Client client1=new Client();
			client1.setComparisonIdentities(new ArrayList<>());
			client1.setDeviceToken(deviceToken1);
			final PublicKeys publicKeys=new PublicKeys();
			final com.kareebo.contacts.server.gora.EncryptionKey encryptionKey=new com.kareebo.contacts.server.gora
				                                                                       .EncryptionKey();
			encryptionKey.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
			encryptionKey.setBuffer(b);
			publicKeys.setEncryption(encryptionKey);
			publicKeys.setVerification(verificationKey);
			client0.setKeys(publicKeys);
			client1.setKeys(publicKeys);
			final UserAgent userAgent=new UserAgent();
			userAgent.setPlatform("");
			userAgent.setVersion("");
			client0.setUserAgent(userAgent);
			client1.setUserAgent(userAgent);
			clients.put(TypeConverter.convert(clientId0.getClient()),client0);
			clients.put(TypeConverter.convert(clientId1.getClient()),client1);
			user.setClients(clients);
			user.setIdentities(new ArrayList<>());
			user.setSentRequests(new ArrayList<>());
			userDataStore.put(user.getId(),user);
		}

		abstract protected void runImpl() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException, TException, InvalidAlgorithmParameterException;
	}

	private abstract class Base extends Signer
	{
		final DataStore<Long,User> userDataStore;
		final DataStore<Long,PendingNotification> pendingNotificationDataStore;
		final Notifier notifierBackend=new Notifier();
		final ClientNotifier clientNotifier;
		final SuggestNewContact suggestNewContact;
		final DataStore<ByteBuffer,HashIdentity> identityDataStore;
		final ClientId clientId=new ClientId(0,0);
		final long deviceToken=0;

		Base() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, GoraException
		{
			userDataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
			pendingNotificationDataStore=DataStoreFactory.getDataStore(Long.class,PendingNotification.class,new Configuration());
			clientNotifier=new ClientNotifier(notifierBackend,pendingNotificationDataStore);
			identityDataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
			suggestNewContact=new SuggestNewContact(userDataStore,identityDataStore,clientNotifier);
			setupMainUser();
		}

		private void setupMainUser() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException,
			                                    InvalidKeyException
		{
			final User user=new User();
			final ByteBuffer b=ByteBuffer.wrap("".getBytes());
			b.mark();
			user.setBlind(b);
			user.setId(clientId.getUser());
			final Map<CharSequence,Client> clients=new HashMap<>(1);
			final Client client=new Client();
			client.setComparisonIdentities(new ArrayList<>());
			client.setDeviceToken(deviceToken);
			final PublicKeys publicKeys=new PublicKeys();
			final com.kareebo.contacts.server.gora.EncryptionKey encryptionKey=new com.kareebo.contacts.server.gora
				                                                                       .EncryptionKey();
			encryptionKey.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
			encryptionKey.setBuffer(b);
			publicKeys.setEncryption(encryptionKey);
			publicKeys.setVerification(verificationKey);
			client.setKeys(publicKeys);
			final UserAgent userAgent=new UserAgent();
			userAgent.setPlatform("");
			userAgent.setVersion("");
			client.setUserAgent(userAgent);
			clients.put(TypeConverter.convert(clientId.getClient()),client);
			user.setClients(clients);
			user.setIdentities(new ArrayList<>());
			user.setSentRequests(new ArrayList<>());
			userDataStore.put(user.getId(),user);
		}
	}
}