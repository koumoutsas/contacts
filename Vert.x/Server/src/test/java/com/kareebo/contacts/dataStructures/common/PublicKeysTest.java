package com.kareebo.contacts.dataStructures.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link com.kareebo.contacts.dataStructures.common.PublicKeys}
 */
public class PublicKeysTest
{
	private PublicKey encryptionKey;
	private PublicKey verificationKey;
	private PublicKeys publicKeys;

	@Before
	public void setUp() throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECCurve curve=new ECCurve.Fp(
			                                  new BigInteger("883423532389192164791648750360308885314476597252960362792450860609699839"), // q
			                                  new BigInteger("7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc",16), // a
			                                  new BigInteger("6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a",16)); // b
		final ECParameterSpec ecSpec=new ECParameterSpec(
			                                                curve,
			                                                curve.decodePoint(Hex.decode("020ffa963cdca8816ccc33b8642bedf905c3d358573d3f27fbbd3b3cb9aaaf")), // G
			                                                new BigInteger("883423532389192164791648750360308884807550341691627752275345424702807307")); // n
		final KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(PublicKeys.getAlgorithm(),PublicKeys.getProvider());
		keyPairGenerator.initialize(ecSpec,new SecureRandom());
		encryptionKey=keyPairGenerator.generateKeyPair().getPublic();
		verificationKey=keyPairGenerator.generateKeyPair().getPublic();
		publicKeys=new PublicKeys(encryptionKey.getEncoded(),verificationKey.getEncoded());
	}

	@Test
	public void testGetEncryption() throws Exception
	{
		assertEquals(encryptionKey,publicKeys.getEncryption());
	}

	@Test
	public void testGetVerification() throws Exception
	{
		assertEquals(verificationKey,publicKeys.getVerification());
	}
}