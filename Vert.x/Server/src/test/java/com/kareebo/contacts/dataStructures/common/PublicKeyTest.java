package com.kareebo.contacts.dataStructures.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

/**
 * Unit test for {@link com.kareebo.contacts.dataStructures.common.PublicKey}
 */
public class PublicKeyTest
{
	private java.security.PublicKey key;
	private PublicKey publicKey;

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
		final String algorithm="ECDSA";
		final KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(algorithm,"BC");
		keyPairGenerator.initialize(ecSpec,new SecureRandom());
		key=keyPairGenerator.generateKeyPair().getPublic();
		publicKey=new PublicKey(key.getEncoded(),algorithm);
	}

	@Test
	public void testGetKey() throws Exception
	{
		final java.security.PublicKey k1=publicKey.getKey();
		final java.security.PublicKey k2=publicKey.getKey();
		org.junit.Assert.assertSame(k1,k2);
		org.junit.Assert.assertEquals(key,k1);
	}
}