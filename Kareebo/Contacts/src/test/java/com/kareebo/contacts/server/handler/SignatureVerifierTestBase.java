package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.IdPair;
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
	final IdPair idPairValid=new IdPair();
	final com.kareebo.contacts.thrift.Signature signature=new com.kareebo.contacts.thrift.Signature();
	PlaintextSerializer plaintext;
	final com.kareebo.contacts.thrift.Signature wrongSignature=new com.kareebo.contacts.thrift.Signature();
	CryptoBuffer verificationKey;
	SignatureVerifier signatureVerifier;
	Client clientValid;

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
		signature.setIds(idPairValid);
		signature.setSignature(ecdsaSign.sign());
		ecdsaSign.update("fgh".getBytes());
		wrongSignature.setIds(idPairValid);
		wrongSignature.setSignature(ecdsaSign.sign());
		verificationKey=TypeConverter.convert(setUpCryptoBuffer(new X509EncodedKeySpec(
			                                                                              keyPair.getPublic().getEncoded
				                                                                                                  ())
			                                                        .getEncoded()));
	}

	public void setUp() throws Exception
	{
		setUpCrypto();
		final long userId=0;
		idPairValid.setClientId(0);
		idPairValid.setUserId(userId);
		final Client client=new Client();
		final UserAgent userAgent=new UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		final PublicKeys publicKeys=new PublicKeys();
		final byte[] buffer={'a','b'};
		publicKeys.setEncryption(TypeConverter.convert(setUpCryptoBuffer(buffer)));
		publicKeys.setVerification(verificationKey);
		client.setUserAgent(userAgent);
		client.setKeys(publicKeys);
		client.setContacts(new ArrayList<HashedContact>());
		final User user=new User();
		user.setClients(new HashMap<CharSequence,Client>());
		final DataStore<Long,User> dataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		dataStore.put(userId,user);
		signatureVerifier=construct(dataStore);
		assertNotNull(signatureVerifier);
		signatureVerifier.put(idPairValid,client);
		clientValid=signatureVerifier.get(idPairValid);
	}

	abstract PlaintextSerializer constructPlaintext();

	com.kareebo.contacts.common.CryptoBuffer setUpCryptoBuffer(final byte[] buffer)
	{
		final com.kareebo.contacts.common.CryptoBuffer cryptoBuffer=new com.kareebo.contacts.common.CryptoBuffer();
		cryptoBuffer.setAlgorithm(com.kareebo.contacts.common.Algorithm.SHA256withECDSAprime239v1);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		cryptoBuffer.setBuffer(byteBuffer);
		return cryptoBuffer;
	}

	abstract SignatureVerifier construct(final DataStore<Long,User> dataStore);
}
