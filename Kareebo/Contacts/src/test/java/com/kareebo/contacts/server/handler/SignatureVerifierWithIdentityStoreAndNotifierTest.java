package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TSerializer;
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
 * Unit test for {@link SignatureVerifierWithIdentityStoreAndNotifier}
 */
public class SignatureVerifierWithIdentityStoreAndNotifierTest
{
	private final Notifier notifierBackend=new Notifier();
	final private VerificationKey verificationKey=new VerificationKey();
	private final ClientId clientId=new ClientId(0,0);
	private DataStore<Long,User> userDataStore;
	private ClientNotifier clientNotifier;
	private KeyPair keyPair;

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
		setUpCrypto();
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

	SignatureBuffer sign(final byte[] buffer,final ClientId clientId) throws NoSuchProviderException,
		                                                                         NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		final Signature ecdsaSign=Signature.getInstance("SHA256withECDSA",Utils.getProvider());
		ecdsaSign.initSign(keyPair.getPrivate());
		ecdsaSign.update(buffer);
		final SignatureBuffer signatureBuffer=new SignatureBuffer();
		signatureBuffer.setBuffer(ecdsaSign.sign());
		signatureBuffer.setAlgorithm(com.kareebo.contacts.thrift.SignatureAlgorithm.SHA256withECDSAprime239v1);
		signatureBuffer.setClient(clientId);
		return signatureBuffer;
	}

	private void setUpCrypto() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g=KeyPairGenerator.getInstance("ECDSA",Utils.getProvider());
		g.initialize(ecSpec,new SecureRandom());
		keyPair=g.generateKeyPair();
		setUpVerificationKey(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()).getEncoded());
	}

	private void setUpVerificationKey(final byte[] buffer)
	{
		verificationKey.setAlgorithm(SignatureAlgorithm.SHA256withECDSAprime239v1);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		verificationKey.setBuffer(byteBuffer);
	}

	private class Notifier implements ClientNotifierBackend
	{
		final Map<Long,Long> sentNotifications=new HashMap<>();

		@Override
		public void notify(final long deviceToken,final long notificationId) throws FailedOperation
		{
			sentNotifications.put(deviceToken,notificationId);
		}
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