package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.server.gora.EncryptedBuffer;
import com.kareebo.contacts.server.gora.PublicKeys;
import com.kareebo.contacts.server.gora.UserAgent;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.VerificationKey;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

/**
 * Base class for all test suites for {@link SignatureVerifier} subclasses
 */
abstract class SignatureVerifierTestBase
{
	final static String ecdsa="ECDSA";
	final ClientId clientIdValid=new ClientId();
	final SignatureBuffer signature=new SignatureBuffer();
	final SignatureBuffer wrongSignature=new SignatureBuffer();
	PlaintextSerializer plaintext;
	com.kareebo.contacts.server.gora.VerificationKey verificationKey;
	SignatureVerifier signatureVerifier;
	Client clientValid;
	UserAgent userAgent;
	private DataStore<Long,User> dataStore;

	public void setUp() throws Exception
	{
		setUpCrypto();
		final long userId=0;
		clientIdValid.setClient(0);
		clientIdValid.setUser(userId);
		userAgent=new UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		final PublicKeys publicKeys=new PublicKeys();
		final byte[] buffer={'a','b'};
		publicKeys.setEncryption(TypeConverter.convert(setUpEncryptionKey(buffer)));
		publicKeys.setVerification(verificationKey);
		final Client client=new Client();
		client.setUserAgent(userAgent);
		client.setKeys(publicKeys);
		client.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
		final User user=new User();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		user.setBlind(byteBuffer);
		final HashMap<CharSequence,Client> clients=new HashMap<>();
		clients.put(TypeConverter.convert(clientIdValid.getClient()),client);
		user.setClients(clients);
		user.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		user.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		dataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		dataStore.put(userId,user);
		signatureVerifier=construct(dataStore);
		assertNotNull(signatureVerifier);
		signatureVerifier.put(clientIdValid,client);
		clientValid=signatureVerifier.get(clientIdValid);
	}

	private void setUpCrypto() throws NoSuchProviderException, NoSuchAlgorithmException,
		                                  InvalidAlgorithmParameterException, InvalidKeyException, SignatureException, InvalidKeySpecException
	{
		plaintext=constructPlaintext();
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g=KeyPairGenerator.getInstance(ecdsa,Utils.getProvider());
		g.initialize(ecSpec,new SecureRandom());
		final KeyPair keyPair=g.generateKeyPair();
		Signature ecdsaSign=Signature.getInstance("SHA256withECDSA",Utils.getProvider());
		ecdsaSign.initSign(keyPair.getPrivate());
		for(final Object a : plaintext.serialize())
		{
			ecdsaSign.update((byte[])a);
		}
		signature.setClient(clientIdValid);
		signature.setBuffer(ecdsaSign.sign());
		ecdsaSign.update("fgh".getBytes());
		wrongSignature.setClient(clientIdValid);
		wrongSignature.setBuffer(ecdsaSign.sign());
		verificationKey=TypeConverter.convert(setUpVerificationKey(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()).getEncoded()));
	}

	EncryptionKey setUpEncryptionKey(final byte[] buffer)
	{
		final EncryptionKey encryptionKey=new EncryptionKey();
		encryptionKey.setAlgorithm(EncryptionAlgorithm.RSA2048);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		encryptionKey.setBuffer(byteBuffer);
		return encryptionKey;
	}

	abstract SignatureVerifier construct(final DataStore<Long,User> dataStore);

	abstract PlaintextSerializer constructPlaintext();

	VerificationKey setUpVerificationKey(final byte[] buffer)
	{
		final VerificationKey verificationKey=new VerificationKey();
		verificationKey.setAlgorithm(SignatureAlgorithm.SHA256withECDSAprime239v1);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		verificationKey.setBuffer(byteBuffer);
		return verificationKey;
	}
}
