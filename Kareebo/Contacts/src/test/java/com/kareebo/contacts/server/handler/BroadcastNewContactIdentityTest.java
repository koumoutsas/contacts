package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.crypto.Utils;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.server.gora.EncryptedBuffer;
import com.kareebo.contacts.server.gora.HashAlgorithm;
import com.kareebo.contacts.server.gora.HashBuffer;
import com.kareebo.contacts.server.gora.PublicKeys;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.UserAgent;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;
import com.kareebo.contacts.thrift.client.jobs.Notification;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit test for {@link BroadcastNewContactIdentity}
 */
public class BroadcastNewContactIdentityTest
{
	/**
	 * Create a client (0,0), create two hash identities uC and uC' mapping to two other users (1 and 2), set their confirmers to various sizes
	 * and check that they're aliased correctly.
	 */
	@Test
	public void testBroadcastNewContactIdentity5() throws Exception
	{
		abstract class Base5 extends SignatureVerifierTestBase
		{
			final HashBufferPair hashBufferPair=new HashBufferPair();
			final DataStore<ByteBuffer,HashIdentity> identityDataStore;

			private Base5() throws Exception
			{
				identityDataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
				setupIdentityDatastore();
				setUp();
			}

			private void setupIdentityDatastore() throws NoSuchAlgorithmException
			{
				final List<String> ids=getIds();
				final HashBuffer uC=new HashBuffer();
				uC.setAlgorithm(HashAlgorithm.SHA256);
				final ByteBuffer b1=ByteBuffer.wrap(ids.get(0).getBytes());
				b1.mark();
				uC.setBuffer(b1);
				hashBufferPair.setUC(TypeConverter.convert(uC));
				final HashIdentity identity1=new HashIdentity();
				identity1.setHash(b1);
				final HashIdentityValue hashIdentityValue1=new HashIdentityValue();
				hashIdentityValue1.setId((long)1);
				identity1.setHashIdentity(hashIdentityValue1);
				final HashBuffer uCP=new HashBuffer();
				uCP.setAlgorithm(HashAlgorithm.SHA256);
				final ByteBuffer b2=ByteBuffer.wrap(ids.get(1).getBytes());
				b2.mark();
				uCP.setBuffer(b2);
				hashBufferPair.setUPrimeC(TypeConverter.convert(uCP));
				final HashIdentity identity2=new HashIdentity();
				identity2.setHash(b1);
				final HashIdentityValue hashIdentityValue2=new HashIdentityValue();
				hashIdentityValue2.setId((long)2);
				identity2.setHashIdentity(hashIdentityValue2);
				setupIdentityDatastoreImplementation(identity1,identity2);
				final List<Boolean> adds=add();
				if(adds.get(0))
				{
					identityDataStore.put(b1,identity1);
				}
				if(adds.get(1))
				{
					identityDataStore.put(b2,identity2);
				}
				identityDataStore.close();
			}

			abstract List<String> getIds();

			abstract void setupIdentityDatastoreImplementation(final HashIdentity identity1,final HashIdentity identity2);

			abstract List<Boolean> add();

			@Override
			SignatureVerifier construct(final DataStore<Long,User> dataStore)
			{
				return new BroadcastNewContactIdentity(dataStore,identityDataStore,null);
			}

			@SuppressWarnings("WeakerAccess")
			void run()
			{
				final Future<Void> result=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity5(hashBufferPair,signature,result);
				check(result);
			}

			abstract void check(final Future<Void> result);

			@Override
			TBase constructPlaintext()
			{
				return hashBufferPair;
			}
		}
		new Base5()
		{
			@Override
			List<String> getIds()
			{
				return Arrays.asList("1","2");
			}

			@Override
			List<Boolean> add()
			{
				return Arrays.asList(true,true);
			}

			@Override
			void check(final Future<Void> result)
			{
				assertTrue(result.succeeded());
				final Object o1=identityDataStore.get(hashBufferPair.getUC().bufferForBuffer()).getHashIdentity();
				final Object o2=identityDataStore.get(hashBufferPair.getUPrimeC().bufferForBuffer()).getHashIdentity();
				assertTrue(o1 instanceof HashIdentityValue);
				assertTrue(o2 instanceof ByteBuffer);
				assertEquals(hashBufferPair.getUC().bufferForBuffer(),o2);
				final List<Long> mergedConfirmers=((HashIdentityValue)o1).getConfirmers();
				assertEquals(4,mergedConfirmers.size());
				assertTrue(mergedConfirmers.containsAll(Arrays.asList((long)2,(long)3,(long)4,(long)5)));
			}

			@Override
			void setupIdentityDatastoreImplementation(final HashIdentity identity1,final HashIdentity identity2)
			{
				final HashIdentityValue hashIdentityValue1=(HashIdentityValue)identity1.getHashIdentity();
				final HashIdentityValue hashIdentityValue2=(HashIdentityValue)identity2.getHashIdentity();
				final List<Long> confirmers1=new ArrayList<>(3);
				confirmers1.add((long)2);
				confirmers1.add((long)3);
				confirmers1.add((long)4);
				hashIdentityValue1.setConfirmers(confirmers1);
				final List<Long> confirmers2=new ArrayList<>(2);
				confirmers1.add((long)4);
				confirmers1.add((long)5);
				hashIdentityValue2.setConfirmers(confirmers2);
			}
		}.run();
		new Base5()
		{
			@Override
			List<Boolean> add()
			{
				return Arrays.asList(true,true);
			}

			@Override
			void check(final Future<Void> result)
			{
				assertTrue(result.succeeded());
				final Object o1=identityDataStore.get(hashBufferPair.getUPrimeC().bufferForBuffer()).getHashIdentity();
				final Object o2=identityDataStore.get(hashBufferPair.getUC().bufferForBuffer()).getHashIdentity();
				assertTrue(o1 instanceof HashIdentityValue);
				assertTrue(o2 instanceof ByteBuffer);
				assertEquals(hashBufferPair.getUPrimeC().bufferForBuffer(),o2);
				final List<Long> mergedConfirmers=((HashIdentityValue)o1).getConfirmers();
				assertEquals(4,mergedConfirmers.size());
				assertTrue(mergedConfirmers.containsAll(Arrays.asList((long)2,(long)3,(long)4,(long)5)));
			}

			@Override
			void setupIdentityDatastoreImplementation(final HashIdentity identity1,final HashIdentity identity2)
			{
				final HashIdentityValue hashIdentityValue1=(HashIdentityValue)identity1.getHashIdentity();
				final HashIdentityValue hashIdentityValue2=(HashIdentityValue)identity2.getHashIdentity();
				final List<Long> confirmers2=new ArrayList<>(3);
				confirmers2.add((long)2);
				confirmers2.add((long)3);
				confirmers2.add((long)4);
				hashIdentityValue2.setConfirmers(confirmers2);
				final List<Long> confirmers1=new ArrayList<>(2);
				confirmers1.add((long)4);
				confirmers1.add((long)5);
				hashIdentityValue1.setConfirmers(confirmers1);
			}

			@Override
			List<String> getIds()
			{
				return Arrays.asList("1","2");
			}
		}.run();
		new Base5()
		{
			@Override
			List<Boolean> add()
			{
				return Arrays.asList(true,true);
			}

			@Override
			void check(final Future<Void> result)
			{
				assertTrue(result.succeeded());
				final Object o1=identityDataStore.get(hashBufferPair.getUPrimeC().bufferForBuffer()).getHashIdentity();
				final Object o2=identityDataStore.get(hashBufferPair.getUC().bufferForBuffer()).getHashIdentity();
				assertTrue(o1 instanceof HashIdentityValue);
				assertTrue(o2 instanceof ByteBuffer);
				assertEquals(hashBufferPair.getUPrimeC().bufferForBuffer(),o2);
				final List<Long> mergedConfirmers=((HashIdentityValue)o1).getConfirmers();
				assertEquals(4,mergedConfirmers.size());
				assertTrue(mergedConfirmers.containsAll(Arrays.asList((long)2,(long)3,(long)4,(long)5)));
			}

			@Override
			void setupIdentityDatastoreImplementation(final HashIdentity identity1,final HashIdentity identity2)
			{
				final HashIdentityValue hashIdentityValue1=(HashIdentityValue)identity1.getHashIdentity();
				final HashIdentityValue hashIdentityValue2=(HashIdentityValue)identity2.getHashIdentity();
				final List<Long> confirmers2=new ArrayList<>(3);
				confirmers2.add((long)2);
				confirmers2.add((long)3);
				hashIdentityValue2.setConfirmers(confirmers2);
				final List<Long> confirmers1=new ArrayList<>(2);
				confirmers1.add((long)4);
				confirmers1.add((long)5);
				hashIdentityValue1.setConfirmers(confirmers1);
			}

			@Override
			List<String> getIds()
			{
				return Arrays.asList("1","2");
			}
		}.run();
		new Base5()
		{
			@Override
			List<Boolean> add()
			{
				return Arrays.asList(true,true);
			}

			@Override
			void check(final Future<Void> result)
			{
				assertTrue(result.succeeded());
				final Object o1=identityDataStore.get(hashBufferPair.getUC().bufferForBuffer()).getHashIdentity();
				final Object o2=identityDataStore.get(hashBufferPair.getUPrimeC().bufferForBuffer()).getHashIdentity();
				assertTrue(o1 instanceof HashIdentityValue);
				assertTrue(o2 instanceof ByteBuffer);
				assertEquals(hashBufferPair.getUC().bufferForBuffer(),o2);
				final List<Long> mergedConfirmers=((HashIdentityValue)o1).getConfirmers();
				assertEquals(4,mergedConfirmers.size());
				assertTrue(mergedConfirmers.containsAll(Arrays.asList((long)2,(long)3,(long)4,(long)5)));
			}

			@Override
			void setupIdentityDatastoreImplementation(final HashIdentity identity1,final HashIdentity identity2)
			{
				final HashIdentityValue hashIdentityValue1=(HashIdentityValue)identity1.getHashIdentity();
				final HashIdentityValue hashIdentityValue2=(HashIdentityValue)identity2.getHashIdentity();
				final List<Long> confirmers2=new ArrayList<>(2);
				confirmers2.add((long)2);
				confirmers2.add((long)3);
				hashIdentityValue2.setConfirmers(confirmers2);
				final List<Long> confirmers1=new ArrayList<>(2);
				confirmers1.add((long)4);
				confirmers1.add((long)5);
				hashIdentityValue1.setConfirmers(confirmers1);
			}

			@Override
			List<String> getIds()
			{
				return Arrays.asList("2","1");
			}
		}.run();
		new Base5()
		{
			@Override
			List<Boolean> add()
			{
				return Arrays.asList(true,true);
			}

			@Override
			void check(final Future<Void> result)
			{
				assertTrue(result.failed());
				final Object o1=identityDataStore.get(hashBufferPair.getUC().bufferForBuffer()).getHashIdentity();
				final Object o2=identityDataStore.get(hashBufferPair.getUPrimeC().bufferForBuffer()).getHashIdentity();
				assertTrue(o1 instanceof HashIdentityValue);
				assertTrue(o2 instanceof HashIdentityValue);
			}

			@Override
			void setupIdentityDatastoreImplementation(final HashIdentity identity1,final HashIdentity identity2)
			{
				final HashIdentityValue hashIdentityValue1=(HashIdentityValue)identity1.getHashIdentity();
				final HashIdentityValue hashIdentityValue2=(HashIdentityValue)identity2.getHashIdentity();
				final List<Long> confirmers2=new ArrayList<>();
				hashIdentityValue2.setConfirmers(confirmers2);
				final List<Long> confirmers1=new ArrayList<>(1);
				hashIdentityValue1.setConfirmers(confirmers1);
			}

			@Override
			List<String> getIds()
			{
				return Arrays.asList("1","1");
			}
		}.run();
		new Base5()
		{
			@Override
			List<Boolean> add()
			{
				return Arrays.asList(true,false);
			}

			@Override
			void check(final Future<Void> result)
			{
				assertTrue(result.failed());
			}

			@Override
			void setupIdentityDatastoreImplementation(final HashIdentity identity1,final HashIdentity identity2)
			{
				final HashIdentityValue hashIdentityValue1=(HashIdentityValue)identity1.getHashIdentity();
				final HashIdentityValue hashIdentityValue2=(HashIdentityValue)identity2.getHashIdentity();
				final List<Long> confirmers2=new ArrayList<>();
				hashIdentityValue2.setConfirmers(confirmers2);
				final List<Long> confirmers1=new ArrayList<>(1);
				hashIdentityValue1.setConfirmers(confirmers1);
			}

			@Override
			List<String> getIds()
			{
				return Arrays.asList("1","2");
			}
		}.run();
		new Base5()
		{
			@Override
			List<Boolean> add()
			{
				return Arrays.asList(false,true);
			}

			@Override
			void check(final Future<Void> result)
			{
				assertTrue(result.failed());
			}

			@Override
			void setupIdentityDatastoreImplementation(final HashIdentity identity1,final HashIdentity identity2)
			{
				final HashIdentityValue hashIdentityValue1=(HashIdentityValue)identity1.getHashIdentity();
				final HashIdentityValue hashIdentityValue2=(HashIdentityValue)identity2.getHashIdentity();
				final List<Long> confirmers2=new ArrayList<>();
				hashIdentityValue2.setConfirmers(confirmers2);
				final List<Long> confirmers1=new ArrayList<>(1);
				hashIdentityValue1.setConfirmers(confirmers1);
			}

			@Override
			List<String> getIds()
			{
				return Arrays.asList("1","2");
			}
		}.run();
	}

	/**
	 * Create a client (0,0), add a notification to pending notifications, sign the notification id and retrieve the EncryptedBufferSignedWithVerificationKey
	 */
	@Test
	public void testBroadcastNewContactIdentity4() throws Exception
	{
		class Base4 extends Base34
		{
			private EncryptedBufferSignedWithVerificationKey encryptedBufferSignedWithVerificationKey;

			private Base4() throws GoraException, NoSuchAlgorithmException, NoSuchProviderException,
				                       InvalidAlgorithmParameterException, InvalidKeyException, SignatureException,
				                       TException
			{
			}

			private void run() throws TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				final Future<EncryptedBufferSignedWithVerificationKey> future=new DefaultFutureResult<>();
				final LongId id=new LongId(notifierBackend.getFirst().getId());
				broadcastNewContactIdentity.broadcastNewContactIdentity4(id,sign(id,clientId),future);
				check(future);
			}

			private void check(final Future<EncryptedBufferSignedWithVerificationKey> future)
			{
				assertTrue(future.succeeded());
				assertEquals(encryptedBufferSignedWithVerificationKey,future.result());
			}

			@Override
			SignatureAlgorithm getSignatureAlgorithm()
			{
				return SignatureAlgorithm.SHA256withECDSAprime239v1;
			}

			@Override
			BroadcastNewContactIdentity construct() throws GoraException
			{
				return new BroadcastNewContactIdentity(userDataStore,DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity
					                                                                                                    .class,new Configuration()),clientNotifier);
			}

			@Override
			void setupUserDatastore() throws FailedOperation
			{
				final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
				b.mark();
				encryptedBufferSignedWithVerificationKey=new
					                                         EncryptedBufferSignedWithVerificationKey();
				encryptedBufferSignedWithVerificationKey.setEncryptedBufferSigned(new EncryptedBufferSigned(new com.kareebo.contacts.thrift.EncryptedBuffer(b,EncryptionAlgorithm.RSA2048,clientId),new SignatureBuffer(b,com.kareebo.contacts.thrift.SignatureAlgorithm.SHA256withECDSAprime239v1,clientId
				)));
				encryptedBufferSignedWithVerificationKey.setVerificationKey(new com.kareebo.contacts.thrift.VerificationKey(b,
					                                                                                                           com.kareebo.contacts.thrift.SignatureAlgorithm.SHA256withECDSAprime239v1
				));
				clientNotifier.put(deviceToken,new NotificationObject(com.kareebo.contacts.client.protocol.BroadcastNewContactIdentity
					                                                      .method4,
					                                                     encryptedBufferSignedWithVerificationKey));
			}
		}
		new Base4().run();
	}

	/**
	 * Create a user (10), create two clients (0,1), create an encrypted buffer for each client and sign it, send it and check if the client
	 * notifier has received the two notifications
	 */
	@Test
	public void testBroadcastNewContactIdentity3() throws Exception
	{
		new Base3()
		{
			void check(final Future<Void> future) throws TException, NoSuchAlgorithmException
			{
				assertTrue(future.succeeded());
				assertEquals(clientNumber,notifierBackend.size());
				for(long i=0;i<clientNumber;++i)
				{
					final EncryptedBufferSignedWithVerificationKey encryptedBufferSignedWithVerificationKey=new
						                                                                                        EncryptedBufferSignedWithVerificationKey();
					final Notification notification=notifierBackend.get(i);
					assertEquals(com.kareebo.contacts.client.protocol.BroadcastNewContactIdentity.method4,notification.getMethod
						                                                                                                   ());
					clientNotifier.get(encryptedBufferSignedWithVerificationKey,notification.getId());
					final EncryptedBufferSignedWithVerificationKey expected=new EncryptedBufferSignedWithVerificationKey(encryptedBuffersMap.get(i),
						                                                                                                    TypeConverter.convert(verificationKey));
					assertEquals(expected,encryptedBufferSignedWithVerificationKey);
				}
			}

			@Override
			BroadcastNewContactIdentity construct() throws GoraException
			{
				return new BroadcastNewContactIdentity(userDataStore,DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity
					                                                                                                    .class,new Configuration()),clientNotifier);
			}

			@Override
			SignatureAlgorithm getSignatureAlgorithm()
			{
				return SignatureAlgorithm.SHA256withECDSAprime239v1;
			}
		}.run();
	}

	/**
	 * Contrived test for 100% coverage. The code that it covers is inaccessible, because if the algorithm is invalid, verify has already
	 * stopped the execution
	 */
	@Test
	public void testBroadcastNewContactIdentity3AlgorithmError() throws Exception
	{
		new Base3()
		{
			@Override
			BroadcastNewContactIdentity construct() throws GoraException
			{
				class BroadcastNewContactIdentityFake extends BroadcastNewContactIdentity
				{
					private BroadcastNewContactIdentityFake(final DataStore<Long,User> dataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
					{
						super(dataStore,identityDatastore,clientNotifier);
					}

					@Override
					void verify(final TBase plaintext,final SignatureBuffer signature,final Reply<?> reply,final After after)
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
				return new BroadcastNewContactIdentityFake(userDataStore,DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity
					                                                                                                        .class,new Configuration()),clientNotifier);
			}

			@Override
			SignatureAlgorithm getSignatureAlgorithm()
			{
				return SignatureAlgorithm.Fake;
			}

			void check(final Future<Void> future) throws FailedOperation, NoSuchAlgorithmException
			{
				assertTrue(future.failed());
				assertEquals(0,notifierBackend.size());
			}
		}.run();
	}

	/**
	 * Create a user (10), create four clients (0,1,2,3) and set an encryption key for each of them.
	 * 0: Create I and IR that match
	 * 1: Create I and IR that match
	 * 2: Don't create I and IR for it
	 * 3: Create I and IR that match, but the comparison identity has invalid algorithm
	 * 4: Create an I and IR for client 4 that doesn't match to any client in the datastore
	 * Execute call 2 and expect success
	 */
	@Test
	public void testBroadcastNewContactIdentity2() throws Exception
	{
		class Base2 extends Base
		{
			final User user=new User();
			private final Set<ClientId> expected=new HashSet<>(2);
			private final Set<EncryptedBufferPair> encryptedBufferPairs=new HashSet<>(4);

			private Base2() throws Exception
			{
				setupDatastores();
				setUp();
				dataStore.put(user.getId(),user);
			}

			/**
			 * Set up the datastores, make call 2 and check the results
			 */
			private void run() throws NoSuchAlgorithmException
			{
				final Future<MapClientIdEncryptionKey> future=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity2(new EncryptedBufferPairSet
					                                                                              (encryptedBufferPairs),signature,
					future);
				check(future);
			}

			/**
			 * Check the result
			 *
			 * @param future The result to be checked
			 */
			private void check(final Future<MapClientIdEncryptionKey> future) throws NoSuchAlgorithmException
			{
				assertTrue(future.succeeded());
				final Map<ClientId,EncryptionKey> reply=future.result().getKeyMap();
				assertEquals(expected.size(),reply.size());
				for(final ClientId clientId : expected)
				{
					final EncryptionKey encryptionKey=reply.get(clientId);
					assertNotNull(encryptionKey);
					final EncryptionKey expectedEncryptionKey=TypeConverter.convert(dataStore.get(clientId.getUser()).getClients().get
						                                                                                                               (TypeConverter.convert
							                                                                                                                              (clientId.getClient())).getKeys().getEncryption());
					assertEquals(expectedEncryptionKey,encryptionKey);
				}
			}

			/**
			 * Creates a client and sets the first comparison identity to a random 3-byte number. Use the first comparison identity to
			 * create the encrypted buffer pair
			 *
			 * @param id        The client id
			 * @param clients   The client set
			 * @param algorithm The algorithm used for the encryption key
			 * @return The created client
			 */
			private Client setupClient(final ClientId id,final HashMap<CharSequence,Client> clients,final com.kareebo.contacts.server
				                                                                                              .gora
				                                                                                              .EncryptionAlgorithm algorithm)
			{
				final Client client=new Client();
				final UserAgent userAgent=new UserAgent();
				userAgent.setPlatform("A");
				userAgent.setVersion("B");
				client.setUserAgent(userAgent);
				final com.kareebo.contacts.server.gora.EncryptionKey encryptionKey=new com.kareebo.contacts.server.gora
					                                                                       .EncryptionKey();
				encryptionKey.setAlgorithm(algorithm);
				final byte[] bytes=new byte[3];
				new SecureRandom().nextBytes(bytes);
				final ByteBuffer b=ByteBuffer.wrap(bytes);
				b.mark();
				encryptionKey.setBuffer(b);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.SHA256withECDSAprime239v1);
				verificationKey.setBuffer(b);
				final PublicKeys publicKeys=new PublicKeys();
				publicKeys.setEncryption(encryptionKey);
				publicKeys.setVerification(verificationKey);
				client.setKeys(publicKeys);
				final List<EncryptedBuffer> comparisonIdentities=new ArrayList<>();
				final EncryptedBuffer comparisonIdentity1=new EncryptedBuffer();
				comparisonIdentity1.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
				comparisonIdentity1.setBuffer(b);
				comparisonIdentities.add(comparisonIdentity1);
				final EncryptedBuffer comparisonIdentity2=new EncryptedBuffer();
				comparisonIdentity2.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
				final ByteBuffer c=ByteBuffer.wrap("12345".getBytes());
				c.mark();
				comparisonIdentity2.setBuffer(c);
				comparisonIdentities.add(comparisonIdentity2);
				client.setComparisonIdentities(comparisonIdentities);
				clients.put(TypeConverter.convert(id.getClient()),client);
				return client;
			}

			private Client setupValidClient(final ClientId id,final HashMap<CharSequence,Client> clients)
			{
				return setupClient(id,clients,com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
			}

			private void setupMatchingClient(final ClientId id,final HashMap<CharSequence,Client> clients,final boolean reverse)
			{
				expected.add(id);
				createEncryptedBufferPair(setupValidClient(id,clients),id,reverse);
			}

			private void createEncryptedBufferPair(final Client client,final ClientId id,final boolean reverse)
			{
				final List<EncryptedBuffer> comparisonIdentities=client.getComparisonIdentities();
				final ByteBuffer comparisonIdentity=comparisonIdentities.iterator().next().getBuffer();
				if(reverse)
				{
					Collections.reverse(comparisonIdentities);
				}
				final byte[] b=com.kareebo.contacts.base.Utils.getBytes(comparisonIdentity);
				final byte[] i=new byte[b.length];
				new SecureRandom().nextBytes(i);
				final ByteBuffer iB=ByteBuffer.wrap(i);
				iB.mark();
				final com.kareebo.contacts.thrift.EncryptedBuffer I=new com.kareebo.contacts.thrift.EncryptedBuffer(iB,
					                                                                                                   EncryptionAlgorithm.RSA2048,id);
				final ByteBuffer iRB=ByteBuffer.wrap(Utils.xor(b,i));
				iRB.mark();
				final com.kareebo.contacts.thrift.EncryptedBuffer IR=new com.kareebo.contacts.thrift.EncryptedBuffer(iRB,
					                                                                                                    EncryptionAlgorithm.RSA2048,id);
				encryptedBufferPairs.add(new EncryptedBufferPair(I,IR));
			}

			private HashMap<CharSequence,Client> setupClients(final Long userId)
			{
				final HashMap<CharSequence,Client> ret=new HashMap<>(4);
				setupMatchingClient(new ClientId(userId,(long)0),ret,true);
				setupMatchingClient(new ClientId(userId,(long)1),ret,false);
				setupValidClient(new ClientId(userId,(long)2),ret);
				final ClientId id3=new ClientId(userId,(long)3);
				createEncryptedBufferPair(setupClient(id3,ret,com.kareebo.contacts.server.gora
					                                              .EncryptionAlgorithm.Fake),id3,false);
				final ByteBuffer byteBuffer=ByteBuffer.wrap("4".getBytes());
				byteBuffer.mark();
				final com.kareebo.contacts.thrift.EncryptedBuffer encryptedBuffer5=new com.kareebo.contacts.thrift.EncryptedBuffer
					                                                                   (byteBuffer,EncryptionAlgorithm.RSA2048,new ClientId(userId,4));
				encryptedBufferPairs.add(new EncryptedBufferPair(encryptedBuffer5,encryptedBuffer5));
				return ret;
			}

			/**
			 * Set up the datastores and and the encrypted buffer pairs
			 */
			private void setupDatastores()
			{
				user.setId((long)10);
				final ByteBuffer b=ByteBuffer.wrap("".getBytes());
				b.mark();
				user.setBlind(b);
				user.setClients(setupClients(user.getId()));
				user.setIdentities(new ArrayList<>());
				user.setSentRequests(new ArrayList<>());
			}

			@Override
			TBase constructPlaintext()
			{
				return new EncryptedBufferPairSet(encryptedBufferPairs);
			}
		}
		new Base2().run();
	}

	/**
	 * Create a user (0), create two clients (0, 1), set an encryption key for each of them and retrieve the encryption keys by user id 0
	 */
	@Test
	public void testBroadcastNewContactIdentity1() throws Exception
	{
		final Long i=(long)10;
		new Base1(i)
		{
			void run() throws NoSuchAlgorithmException
			{
				final Map<ClientId,EncryptionKey> expected=new HashMap<>(2);
				final User newUser=new User();
				newUser.setId(i);
				final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
				b.mark();
				newUser.setBlind(b);
				newUser.setIdentities(new ArrayList<>());
				newUser.setSentRequests(new ArrayList<>());
				final Map<CharSequence,Client> clients=new HashMap<>(expected.size());
				final Client client0=new Client();
				final List<EncryptedBuffer> comparisonIdentities=new ArrayList<>();
				client0.setComparisonIdentities(comparisonIdentities);
				client0.setUserAgent(userAgent);
				final EncryptionKey encryptionKey0=new EncryptionKey();
				encryptionKey0.setAlgorithm(EncryptionAlgorithm.RSA2048);
				encryptionKey0.setBuffer(b);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
				verificationKey.setBuffer(b);
				final PublicKeys publicKeys0=new PublicKeys();
				publicKeys0.setEncryption(TypeConverter.convert(encryptionKey0));
				publicKeys0.setVerification(verificationKey);
				client0.setKeys(publicKeys0);
				clients.put(TypeConverter.convert(0),client0);
				expected.put(new ClientId(newUser.getId(),0),encryptionKey0);
				final Client client1=new Client();
				clients.put(TypeConverter.convert(1),client1);
				client1.setComparisonIdentities(comparisonIdentities);
				client1.setUserAgent(userAgent);
				final EncryptionKey encryptionKey1=new EncryptionKey();
				encryptionKey1.setAlgorithm(EncryptionAlgorithm.RSA2048);
				final ByteBuffer c=ByteBuffer.wrap("2".getBytes());
				c.mark();
				encryptionKey1.setBuffer(c);
				final PublicKeys publicKeys1=new PublicKeys();
				publicKeys1.setEncryption(TypeConverter.convert(encryptionKey1));
				publicKeys1.setVerification(verificationKey);
				client1.setKeys(publicKeys1);
				expected.put(new ClientId(newUser.getId(),1),encryptionKey1);
				newUser.setClients(clients);
				dataStore.put(i,newUser);
				final Future<MapClientIdEncryptionKey> result=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity1(new LongId(i),signature,result);
				assertTrue(result.succeeded());
				final Map<ClientId,EncryptionKey> reply=result.result().getKeyMap();
				reply.values().forEach(e->e.bufferForBuffer().rewind());
				assertEquals(expected,reply);
			}
		}.run();
	}

	/**
	 * Create a user (0), add a client (0) with an encryption key with invalid algorithm and expect a failure
	 */
	@Test
	public void testBroadcastNewContactIdentity1Error() throws Exception
	{
		final Long i=(long)10;
		new Base1(i)
		{
			void run() throws NoSuchAlgorithmException
			{
				final User newUser=new User();
				newUser.setId(i);
				final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
				b.mark();
				newUser.setBlind(b);
				newUser.setIdentities(new ArrayList<>());
				newUser.setSentRequests(new ArrayList<>());
				final Map<CharSequence,Client> clients=new HashMap<>(1);
				final Client client0=new Client();
				client0.setComparisonIdentities(new ArrayList<>());
				client0.setUserAgent(userAgent);
				final com.kareebo.contacts.server.gora.EncryptionKey encryptionKey0=new com.kareebo.contacts.server.gora.EncryptionKey();
				encryptionKey0.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.Fake);
				encryptionKey0.setBuffer(b);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
				verificationKey.setBuffer(b);
				final PublicKeys publicKeys0=new PublicKeys();
				publicKeys0.setEncryption(encryptionKey0);
				publicKeys0.setVerification(verificationKey);
				client0.setKeys(publicKeys0);
				clients.put(TypeConverter.convert(0),client0);
				newUser.setClients(clients);
				dataStore.put(i,newUser);
				final Future<MapClientIdEncryptionKey> result=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity1(new LongId(i),signature,result);
				assertTrue(result.failed());
				//noinspection ThrowableResultOfMethodCallIgnored
				assertEquals(FailedOperation.class,result.cause().getClass());
			}
		}.run();
	}

	abstract class Base34 extends Signer
	{
		protected final ClientId clientId=new ClientId(0,0);
		final Notifier notifierBackend=new Notifier();
		final ClientNotifier clientNotifier;
		final BroadcastNewContactIdentity broadcastNewContactIdentity;
		final long deviceToken=0;
		final DataStore<Long,User> userDataStore;
		private final DataStore<Long,PendingNotification> pendingNotificationDataStore;

		private Base34() throws GoraException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
			                        InvalidKeyException, SignatureException, TException
		{
			verificationKey.setAlgorithm(getSignatureAlgorithm());
			userDataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
			pendingNotificationDataStore=DataStoreFactory.getDataStore(Long.class,PendingNotification.class,new Configuration());
			clientNotifier=new ClientNotifier(notifierBackend,pendingNotificationDataStore);
			broadcastNewContactIdentity=construct();
			setupMainUser();
			setupUserDatastore();
		}

		abstract SignatureAlgorithm getSignatureAlgorithm();

		abstract BroadcastNewContactIdentity construct() throws GoraException;

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

		abstract void setupUserDatastore() throws TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException;
	}

	private abstract class Base3 extends Base34
	{
		static final int clientNumber=2;
		Map<Long,EncryptedBufferSigned> encryptedBuffersMap;
		private Set<EncryptedBufferSigned> encryptedBuffers;

		private Base3() throws GoraException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
			                       InvalidKeyException, SignatureException, TException
		{
		}

		/**
		 * Set up the datastores, make call 2 and check the results
		 */
		void run() throws NoSuchAlgorithmException, TException
		{
			final Future<Void> future=new DefaultFutureResult<>();
			broadcastNewContactIdentity.broadcastNewContactIdentity3(encryptedBuffers,future);
			check(future);
		}

		abstract void check(final Future<Void> future) throws TException, NoSuchAlgorithmException;

		@Override
		void setupUserDatastore() throws TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
		{
			encryptedBuffers=new HashSet<>(clientNumber);
			encryptedBuffersMap=new HashMap<>(clientNumber);
			final User user=new User();
			final ByteBuffer b=ByteBuffer.wrap("".getBytes());
			b.mark();
			user.setBlind(b);
			user.setId((long)10);
			final Map<CharSequence,Client> clients=new HashMap<>(clientNumber);
			for(long i=0;i<clientNumber;++i)
			{
				final Client client=new Client();
				client.setComparisonIdentities(new ArrayList<>());
				client.setDeviceToken(i);
				final PublicKeys publicKeys=new PublicKeys();
				final com.kareebo.contacts.server.gora.EncryptionKey encryptionKey=new com.kareebo.contacts.server.gora
					                                                                       .EncryptionKey();
				encryptionKey.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
				encryptionKey.setBuffer(b);
				publicKeys.setEncryption(encryptionKey);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.SHA256withECDSAprime239v1);
				verificationKey.setBuffer(b);
				publicKeys.setVerification(verificationKey);
				client.setKeys(publicKeys);
				final UserAgent userAgent=new UserAgent();
				userAgent.setPlatform("");
				userAgent.setVersion("");
				client.setUserAgent(userAgent);
				clients.put(TypeConverter.convert(i),client);
				final ByteBuffer buffer=ByteBuffer.wrap(TypeConverter.convert(i).toString().getBytes());
				buffer.mark();
				final ClientId clientId=new ClientId(user.getId(),i);
				final com.kareebo.contacts.thrift.EncryptedBuffer encryptedBuffer=new com.kareebo.contacts.thrift
					                                                                      .EncryptedBuffer(buffer,
						                                                                                      EncryptionAlgorithm.RSA2048,clientId);
				final EncryptedBufferSigned encryptedBufferSigned=new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer,this
					                                                                                                                 .clientId));
				encryptedBuffers.add(encryptedBufferSigned);
				encryptedBuffersMap.put(i,encryptedBufferSigned);
			}
			user.setClients(clients);
			user.setIdentities(new ArrayList<>());
			user.setSentRequests(new ArrayList<>());
			userDataStore.put(user.getId(),user);
		}
	}

	/**
	 * Base class for all bases
	 */
	abstract private class Base extends SignatureVerifierTestBase
	{
		final DataStore<ByteBuffer,HashIdentity> identityDataStore;
		final ClientNotifier clientNotifier=new ClientNotifier(null,null);

		Base() throws GoraException
		{
			identityDataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
		}

		@Override
		SignatureVerifier construct(final DataStore<Long,User> dataStore)
		{
			return new BroadcastNewContactIdentity(dataStore,identityDataStore,clientNotifier);
		}
	}

	/**
	 * Base class for all tests for call 1
	 */
	private class Base1 extends Base
	{
		final private Long i;

		Base1(final Long i) throws Exception
		{
			this.i=i;
			setUp();
		}

		@Override
		TBase constructPlaintext()
		{
			return new LongId(i);
		}
	}
}