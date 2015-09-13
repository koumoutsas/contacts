package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.HashBufferPlaintextSerializer;
import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.base.RegisterIdentityInputPlaintextSerializer;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.server.gora.EncryptedBuffer;
import com.kareebo.contacts.server.gora.EncryptionAlgorithm;
import com.kareebo.contacts.server.gora.EncryptionKey;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.PublicKeys;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit test for {@link RegisterIdentity}
 */
public class RegisterIdentityTest
{
	@Test
	public void testRegisterIdentity1() throws Exception
	{
		new Base1Succeeding()
		{
			@Override
			void setupDatastores()
			{
			}
		}.run();
	}

	@Test
	public void testRegisterIdentity1Existing() throws Exception
	{
		new Base1Succeeding()
		{
			private Long newUserId;

			@Override
			void setupDatastores()
			{
				final HashIdentityValue value=new HashIdentityValue();
				newUserId=clientIdValid.getUser()+1;
				value.setId(newUserId);
				value.setConfirmers(new ArrayList<Long>());
				final HashIdentity identity=new HashIdentity();
				final ByteBuffer key=uA.bufferForBuffer();
				identity.setHash(key);
				identity.setHashIdentity(value);
				identityDataStore.put(key,identity);
				final User newUser=new User();
				newUser.setId(newUserId);
				final ByteBuffer b=ByteBuffer.wrap("".getBytes());
				b.mark();
				newUser.setBlind(b);
				newUser.setClients(new HashMap<CharSequence,Client>());
				newUser.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
				newUser.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
				dataStore.put(newUserId,newUser);
			}
		}.run();
	}

	@Test
	public void testRegisterIdentity1Error() throws Exception
	{
		new Base1()
		{
			private Long newUserId;

			void run() throws FailedOperation
			{
				final Future<RegisterIdentityReply> result=new DefaultFutureResult<>();
				((RegisterIdentity)signatureVerifier).registerIdentity1(uA,signature,result);
				assertTrue(result.failed());
				assertNull(result.result());
			}

			@Override
			void setupDatastores()
			{
				final HashIdentityValue value=new HashIdentityValue();
				newUserId=clientIdValid.getUser()+1;
				value.setId(newUserId);
				value.setConfirmers(new ArrayList<Long>());
				final HashIdentity identity=new HashIdentity();
				final ByteBuffer key=uA.bufferForBuffer();
				identity.setHash(key);
				identity.setHashIdentity(value);
				identityDataStore.put(key,identity);
			}
		}.run();
	}

	@Test
	public void registerIdentity2() throws Exception
	{
		final Long newUserId=(long)0;
		final User newUser=new User();
		newUser.setId(newUserId);
		final ByteBuffer b=ByteBuffer.wrap("a".getBytes());
		b.mark();
		newUser.setBlind(b);
		newUser.setClients(new HashMap<CharSequence,Client>());
		newUser.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		newUser.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		final DataStore<Long,User> dataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		dataStore.put(newUserId,newUser);
		final Future<RegisterIdentityReply> result=new DefaultFutureResult<>();
		final RegisterIdentity handler=new RegisterIdentity(dataStore,null);
		handler.registerIdentity2(newUserId,result);
		assertTrue(result.succeeded());
		final RegisterIdentityReply reply=result.result();
		assertEquals(b,reply.bufferForBlind());
		assertTrue(handler.clientDBAccessor.get(newUserId).getClients().containsKey(TypeConverter.convert(reply.getId())));
	}

	@Test
	public void registerIdentity2Failed() throws Exception
	{
		final Long newUserId=(long)0;
		final User newUser=new User();
		newUser.setId(newUserId);
		final ByteBuffer b=ByteBuffer.wrap("a".getBytes());
		b.mark();
		newUser.setBlind(b);
		class MyMap extends HashMap<CharSequence,Client>
		{
			@Override
			public boolean containsKey(Object O)
			{
				return true;
			}
		}
		newUser.setClients(new MyMap());
		newUser.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		newUser.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		final DataStore<Long,User> dataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		dataStore.put(newUserId,newUser);
		final Future<RegisterIdentityReply> result=new DefaultFutureResult<>();
		final RegisterIdentity handler=new RegisterIdentity(dataStore,null);
		handler.registerIdentity2(newUserId,result);
		assertTrue(result.failed());
		assertNull(result.result());
	}

	@Test
	public void registerIdentity3() throws Exception
	{
		new Base3Succeeding()
		{
			void run() throws NoSuchAlgorithmException
			{
				final Future<Void> result=new DefaultFutureResult<>();
				registerIdentity.registerIdentity3(registerIdentityInput,signature,result);
				assertTrue(result.succeeded());
				final User userRetrieved=userDataStore.get(user);
				assertNotNull(userRetrieved);
				final Client clientRetrieved=userRetrieved.getClients().get(TypeConverter.convert(client));
				assertNotNull(clientRetrieved);
				final PublicKeys keysRetrieved=TypeConverter.convert(clientRetrieved.getKeys());
				keysRetrieved.getEncryption().bufferForBuffer().rewind();
				keysRetrieved.getEncryption().bufferForBuffer().mark();
				keysRetrieved.getVerification().bufferForBuffer().rewind();
				keysRetrieved.getVerification().bufferForBuffer().mark();
				assertEquals(publicKeys,keysRetrieved);
				assertEquals(userAgent,TypeConverter.convert(clientRetrieved.getUserAgent()));
				assertEquals(confirmers,((HashIdentityValue)identityDataStore.get(uC.bufferForBuffer()).getHashIdentity())
					                        .getConfirmers());
				assertEquals(uC.bufferForBuffer(),identityDataStore.get(uA.bufferForBuffer()).getHashIdentity());
				assertEquals(deviceToken,(long)clientRetrieved.getDeviceToken());
				for(final HashBuffer h : uSet)
				{
					final HashIdentityValue value=(HashIdentityValue)identityDataStore.get(h.bufferForBuffer()).getHashIdentity();
					assertNotNull(value);
					assertEquals(user,value.getId());
					if(h!=uC)
					{
						assertTrue(value.getConfirmers().isEmpty());
					}
					assertTrue(userRetrieved.getIdentities().contains(TypeConverter.convert(h)));
				}
			}
		}.run();
	}

	@Test
	public void registerIdentity3WrongClientId() throws Exception
	{
		new Base3WrongUser()
		{
			@Override
			Long wrongUser()
			{
				return user+1;
			}
		}.run();
	}

	@Test
	public void registerIdentity3NoClient() throws Exception
	{
		new Base3WrongUser()
		{
			@Override
			Long wrongUser()
			{
				return user;
			}
		}.run();
	}

	@Test
	public void registerIdentity3NoSuchAlgorithm() throws Exception
	{
		new Base3Failing()
		{
			@Override
			void setupRegisterIdentityInput() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
			{
				final ByteBuffer a=ByteBuffer.wrap("1".getBytes());
				a.mark();
				uA=new HashBuffer(a,HashAlgorithm.SHA256);
				final ByteBuffer b=ByteBuffer.wrap("2".getBytes());
				b.mark();
				final HashBuffer uB=new HashBuffer(b,HashAlgorithm.SHA256);
				final ByteBuffer c=ByteBuffer.wrap("3".getBytes());
				c.mark();
				uC=new HashBuffer(c,HashAlgorithm.SHA256);
				uSet=new HashSet<>(2);
				uSet.add(uB);
				uSet.add(uC);
				userAgent.setPlatform("A");
				userAgent.setVersion("B");
				publicKeys.getEncryption().setAlgorithm(com.kareebo.contacts.thrift.EncryptionAlgorithm.Fake);
				final HashIdentity hashIdentity=new HashIdentity();
				hashIdentity.setHash(a);
				final HashIdentityValue value=new HashIdentityValue();
				value.setId(user);
				value.setConfirmers(confirmers);
				hashIdentity.setHashIdentity(value);
				identityDataStore.put(a,hashIdentity);
				final User userObject=new User();
				userObject.setBlind(a);
				final Client clientObject=new Client();
				clientObject.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
				final com.kareebo.contacts.server.gora.PublicKeys publicKeys1=new com.kareebo.contacts.server.gora.PublicKeys();
				final EncryptionKey encryptionKey=new EncryptionKey();
				encryptionKey.setAlgorithm(EncryptionAlgorithm.Fake);
				encryptionKey.setBuffer(a);
				publicKeys1.setEncryption(encryptionKey);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
				verificationKey.setBuffer(a);
				publicKeys1.setVerification(verificationKey);
				clientObject.setKeys(publicKeys1);
				final com.kareebo.contacts.server.gora.UserAgent userAgent1=new com.kareebo.contacts.server.gora.UserAgent();
				userAgent1.setPlatform("");
				userAgent1.setVersion("");
				clientObject.setUserAgent(userAgent1);
				final Map<CharSequence,Client> clients=new HashMap<>(1);
				clients.put(TypeConverter.convert(client),clientObject);
				userObject.setClients(clients);
				userObject.setId(user);
				userObject.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
				userObject.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
				userDataStore.put(user,userObject);
			}
		}.run();
	}

	@Test
	public void registerIdentity3NoUJ() throws Exception
	{
		new Base3Failing()
		{
			@Override
			void setupRegisterIdentityInput() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
			{
				final ByteBuffer a=ByteBuffer.wrap("1".getBytes());
				a.mark();
				uA=new HashBuffer(a,HashAlgorithm.SHA256);
				final ByteBuffer b=ByteBuffer.wrap("2".getBytes());
				b.mark();
				final HashBuffer uB=new HashBuffer(b,HashAlgorithm.SHA256);
				final ByteBuffer c=ByteBuffer.wrap("3".getBytes());
				c.mark();
				uC=new HashBuffer(c,HashAlgorithm.SHA256);
				uSet=new HashSet<>(2);
				uSet.add(uB);
				userAgent.setPlatform("A");
				userAgent.setVersion("B");
				final HashIdentity hashIdentity=new HashIdentity();
				hashIdentity.setHash(a);
				final HashIdentityValue value=new HashIdentityValue();
				value.setId(user);
				value.setConfirmers(confirmers);
				hashIdentity.setHashIdentity(value);
				identityDataStore.put(a,hashIdentity);
				final User userObject=new User();
				userObject.setBlind(a);
				final Client clientObject=new Client();
				clientObject.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
				final com.kareebo.contacts.server.gora.PublicKeys publicKeys1=new com.kareebo.contacts.server.gora.PublicKeys();
				final EncryptionKey encryptionKey=new EncryptionKey();
				encryptionKey.setAlgorithm(EncryptionAlgorithm.Fake);
				encryptionKey.setBuffer(a);
				publicKeys1.setEncryption(encryptionKey);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
				verificationKey.setBuffer(a);
				publicKeys1.setVerification(verificationKey);
				clientObject.setKeys(publicKeys1);
				final com.kareebo.contacts.server.gora.UserAgent userAgent1=new com.kareebo.contacts.server.gora.UserAgent();
				userAgent1.setPlatform("");
				userAgent1.setVersion("");
				clientObject.setUserAgent(userAgent1);
				final Map<CharSequence,Client> clients=new HashMap<>(1);
				clients.put(TypeConverter.convert(client),clientObject);
				userObject.setClients(clients);
				userObject.setId(user);
				userObject.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
				userObject.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
				userDataStore.put(user,userObject);
			}
		}.run();
	}

	@Test
	public void registerIdentity3NoSuchAlgorithmHash() throws Exception
	{
		new Base3Failing()
		{
			@Override
			void setupRegisterIdentityInput() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
			{
				final ByteBuffer a=ByteBuffer.wrap("1".getBytes());
				a.mark();
				uA=new HashBuffer(a,HashAlgorithm.SHA256);
				final ByteBuffer b=ByteBuffer.wrap("2".getBytes());
				b.mark();
				final HashBuffer uB=new HashBuffer(b,HashAlgorithm.SHA256);
				final ByteBuffer c=ByteBuffer.wrap("3".getBytes());
				c.mark();
				uC=new HashBuffer(c,HashAlgorithm.Fake);
				uSet=new HashSet<>(2);
				uSet.add(uB);
				uSet.add(uC);
				userAgent.setPlatform("A");
				userAgent.setVersion("B");
				final HashIdentity hashIdentity=new HashIdentity();
				hashIdentity.setHash(a);
				final HashIdentityValue value=new HashIdentityValue();
				value.setId(user);
				value.setConfirmers(confirmers);
				hashIdentity.setHashIdentity(value);
				identityDataStore.put(a,hashIdentity);
				final User userObject=new User();
				userObject.setBlind(a);
				final Client clientObject=new Client();
				clientObject.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
				final com.kareebo.contacts.server.gora.PublicKeys publicKeys1=new com.kareebo.contacts.server.gora.PublicKeys();
				final EncryptionKey encryptionKey=new EncryptionKey();
				encryptionKey.setAlgorithm(EncryptionAlgorithm.Fake);
				encryptionKey.setBuffer(a);
				publicKeys1.setEncryption(encryptionKey);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
				verificationKey.setBuffer(a);
				publicKeys1.setVerification(verificationKey);
				clientObject.setKeys(publicKeys1);
				final com.kareebo.contacts.server.gora.UserAgent userAgent1=new com.kareebo.contacts.server.gora.UserAgent();
				userAgent1.setPlatform("");
				userAgent1.setVersion("");
				clientObject.setUserAgent(userAgent1);
				final Map<CharSequence,Client> clients=new HashMap<>(1);
				clients.put(TypeConverter.convert(client),clientObject);
				userObject.setClients(clients);
				userObject.setId(user);
				userObject.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
				userObject.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
				userDataStore.put(user,userObject);
			}
		}.run();
	}

	abstract private class Base3WrongUser extends Base3Failing
	{
		private Base3WrongUser() throws Exception
		{
		}

		@Override
		void setupRegisterIdentityInput() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
		{
			final ByteBuffer a=ByteBuffer.wrap("1".getBytes());
			a.mark();
			uA=new HashBuffer(a,HashAlgorithm.SHA256);
			final ByteBuffer b=ByteBuffer.wrap("2".getBytes());
			b.mark();
			final HashBuffer uB=new HashBuffer(b,HashAlgorithm.SHA256);
			final ByteBuffer c=ByteBuffer.wrap("3".getBytes());
			c.mark();
			uC=new HashBuffer(c,HashAlgorithm.SHA256);
			uSet=new HashSet<>(2);
			uSet.add(uB);
			uSet.add(uC);
			userAgent.setPlatform("A");
			userAgent.setVersion("B");
			final HashIdentity hashIdentity=new HashIdentity();
			hashIdentity.setHash(a);
			final HashIdentityValue value=new HashIdentityValue();
			value.setId(wrongUser());
			value.setConfirmers(confirmers);
			hashIdentity.setHashIdentity(value);
			identityDataStore.put(a,hashIdentity);
		}

		abstract Long wrongUser();
	}

	abstract private class Base extends SignatureVerifierTestBase
	{
		final DataStore<ByteBuffer,HashIdentity> identityDataStore;

		private Base() throws Exception
		{
			identityDataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
			setUp();
		}

		@Override
		SignatureVerifier construct(final DataStore<Long,User> dataStore)
		{
			return new RegisterIdentity(dataStore,identityDataStore);
		}
	}

	abstract private class Base3
	{
		final static String ecdsa="ECDSA";
		final DataStore<ByteBuffer,HashIdentity> identityDataStore;
		final DataStore<Long,User> userDataStore;
		final SignatureBuffer signature=new SignatureBuffer();
		final Long user=(long)0;
		final Long client=(long)0;
		final ClientId clientId=new ClientId();
		final RegisterIdentityInput registerIdentityInput=new RegisterIdentityInput();
		final PublicKeys publicKeys=new PublicKeys();
		final UserAgent userAgent=new UserAgent();
		final RegisterIdentity registerIdentity;
		final List<Long> confirmers=new ArrayList<>(1);
		final long deviceToken=10;
		HashBuffer uA;
		HashBuffer uC;
		Set<HashBuffer> uSet;

		private Base3() throws Exception
		{
			identityDataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
			userDataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
			registerIdentity=new RegisterIdentity(userDataStore,identityDataStore);
			constructPlaintext();
		}

		PlaintextSerializer constructPlaintext() throws SignatureException, InvalidKeyException
		{
			try
			{
				clientId.setUser(user);
				clientId.setClient(client);
				publicKeys.setEncryption(SignatureVerifierTestBase.setUpEncryptionKey("ab".getBytes()));
				Security.addProvider(new BouncyCastleProvider());
				final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
				final KeyPairGenerator g=KeyPairGenerator.getInstance(ecdsa,Utils.getProvider());
				g.initialize(ecSpec,new SecureRandom());
				final KeyPair keyPair=g.generateKeyPair
					                        ();
				publicKeys.setVerification(SignatureVerifierTestBase.setUpVerificationKey(new X509EncodedKeySpec(keyPair.getPublic()
					                                                                                                 .getEncoded
						                                                                                                  ()).getEncoded()));
				setupRegisterIdentityInput();
				registerIdentityInput.setPublicKeys(publicKeys);
				registerIdentityInput.setUA(uA);
				registerIdentityInput.setUJ(uC);
				registerIdentityInput.setUserIdA(user);
				registerIdentityInput.setUSet(uSet);
				registerIdentityInput.setUserAgent(userAgent);
				registerIdentityInput.setDeviceToken(deviceToken);
				final Signature ecdsaSign=Signature.getInstance("SHA256withECDSA",Utils.getProvider());
				ecdsaSign.initSign(keyPair.getPrivate());
				for(final Object a : new RegisterIdentityInputPlaintextSerializer(registerIdentityInput).serialize())
				{
					ecdsaSign.update((byte[])a);
				}
				signature.setClient(clientId);
				signature.setBuffer(ecdsaSign.sign());
			}
			catch(NoSuchAlgorithmException|InvalidAlgorithmParameterException|NoSuchProviderException|FailedOperation e)
			{
				e.printStackTrace();
				fail();
			}
			return new RegisterIdentityInputPlaintextSerializer(registerIdentityInput);
		}

		abstract void setupRegisterIdentityInput() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException;
	}

	private class Base3Succeeding extends Base3
	{
		private Base3Succeeding() throws Exception
		{
		}

		@Override
		void setupRegisterIdentityInput() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
		{
			final ByteBuffer a=ByteBuffer.wrap("1".getBytes());
			a.mark();
			uA=new HashBuffer(a,HashAlgorithm.SHA256);
			final ByteBuffer b=ByteBuffer.wrap("2".getBytes());
			b.mark();
			final HashBuffer uB=new HashBuffer(b,HashAlgorithm.SHA256);
			final ByteBuffer c=ByteBuffer.wrap("3".getBytes());
			c.mark();
			uC=new HashBuffer(c,HashAlgorithm.SHA256);
			uSet=new HashSet<>(2);
			uSet.add(uB);
			uSet.add(uC);
			userAgent.setPlatform("A");
			userAgent.setVersion("B");
			final HashIdentity hashIdentity=new HashIdentity();
			hashIdentity.setHash(a);
			final HashIdentityValue value=new HashIdentityValue();
			value.setId(user);
			confirmers.add((long)10);
			value.setConfirmers(confirmers);
			hashIdentity.setHashIdentity(value);
			identityDataStore.put(a,hashIdentity);
			final User userObject=new User();
			userObject.setBlind(a);
			final Client clientObject=new Client();
			clientObject.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
			final com.kareebo.contacts.server.gora.PublicKeys publicKeys1=new com.kareebo.contacts.server.gora.PublicKeys();
			final EncryptionKey encryptionKey=new EncryptionKey();
			encryptionKey.setAlgorithm(EncryptionAlgorithm.Fake);
			encryptionKey.setBuffer(a);
			publicKeys1.setEncryption(encryptionKey);
			final VerificationKey verificationKey=new VerificationKey();
			verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
			verificationKey.setBuffer(a);
			publicKeys1.setVerification(verificationKey);
			clientObject.setKeys(publicKeys1);
			final com.kareebo.contacts.server.gora.UserAgent userAgent1=new com.kareebo.contacts.server.gora.UserAgent();
			userAgent1.setPlatform("");
			userAgent1.setVersion("");
			clientObject.setUserAgent(userAgent1);
			final Map<CharSequence,Client> clients=new HashMap<>(1);
			clients.put(TypeConverter.convert(client),clientObject);
			userObject.setClients(clients);
			userObject.setId(user);
			userObject.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
			userObject.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
			userDataStore.put(user,userObject);
		}
	}

	abstract private class Base3Failing extends Base3
	{
		private Base3Failing() throws Exception
		{
		}

		void run()
		{
			final Future<Void> result=new DefaultFutureResult<>();
			registerIdentity.registerIdentity3(registerIdentityInput,signature,result);
			assertTrue(result.failed());
			//noinspection ThrowableResultOfMethodCallIgnored
			assertEquals(FailedOperation.class,result.cause().getClass());
		}
	}

	abstract private class Base1 extends Base
	{
		HashBuffer uA;

		private Base1() throws Exception
		{
		}

		@Override
		PlaintextSerializer constructPlaintext()
		{
			final ByteBuffer b=ByteBuffer.wrap("a".getBytes());
			b.mark();
			uA=new HashBuffer(b,HashAlgorithm.SHA256);
			setupDatastores();
			return new HashBufferPlaintextSerializer(uA);
		}

		abstract void setupDatastores();
	}

	abstract private class Base1Succeeding extends Base1
	{
		private Base1Succeeding() throws Exception
		{
		}

		void run() throws FailedOperation
		{
			final Future<RegisterIdentityReply> result=new DefaultFutureResult<>();
			((RegisterIdentity)signatureVerifier).registerIdentity1(uA,signature,result);
			assertTrue(result.succeeded());
			final HashIdentityRetriever retriever=new HashIdentityRetriever(identityDataStore);
			final HashIdentityValue value=retriever.get(uA.bufferForBuffer());
			final Long userId=value.getId();
			assertNotNull(userId);
			final RegisterIdentityReply reply=result.result();
			assertEquals(userId.longValue(),reply.getId());
			final User user=signatureVerifier.clientDBAccessor.get(userId);
			final ByteBuffer expectedBlind=user.getBlind();
			expectedBlind.rewind();
			expectedBlind.mark();
			assertEquals(expectedBlind,reply.bufferForBlind());
			assertTrue(value.getConfirmers().contains(clientIdValid.getUser()));
		}
	}
}