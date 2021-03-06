package com.kareebo.contacts.crypto;

import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.VerificationKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.*;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link Utils}
 */
public class UtilsTest
{
	final static private String ecdsa="ECDSA";

	static
	{
		// For 100% coverage
		new Utils();
	}

	private final byte[] plaintext="abc".getBytes();
	private VerificationKey verificationKey;
	private ByteBuffer signature;

	@Before
	public void setUp() throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g=KeyPairGenerator.getInstance(ecdsa,Utils.getProvider());
		g.initialize(ecSpec,new SecureRandom());
		final TestKeyPair keyPair=new TestKeyPair();
		Signature ecdsaSign=Signature.getInstance("SHA512withECDSA",Utils.getProvider());
		ecdsaSign.initSign(keyPair.getPrivate());
		ecdsaSign.update(plaintext);
		signature=ByteBuffer.wrap(ecdsaSign.sign());
		signature.mark();
		verificationKey=keyPair.verificationKey();
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testVerifySignature() throws Exception
	{
		signature.rewind();
		assertTrue(Utils.verifySignature(verificationKey,signature,plaintext));
		final byte[] plainTextWrong="def".getBytes();
		signature.rewind();
		assertFalse(Utils.verifySignature(verificationKey,signature,plainTextWrong));
		final VerificationKey wrongKey=verificationKey;
		wrongKey.setAlgorithm(SignatureAlgorithm.Fake);
		Utils.verifySignature(wrongKey,signature,plaintext);
	}

	@Test
	public void testXor() throws Exception
	{
		testXor(new byte[]{1,0},new byte[]{0,1},new byte[]{1,1});
		testXor(new byte[]{1,0},new byte[]{1,0},new byte[]{0,0});
		testXor(new byte[]{1,0,1},new byte[]{1,0},new byte[]{0,0,1});
	}

	private void testXor(final byte[] a,final byte[] b,final byte[] x)
	{
		assertArrayEquals(x,Utils.xor(a,b));
		assertArrayEquals(x,Utils.xor(b,a));
	}

	@Test
	public void testGetSignatureAlgorithm() throws Exception
	{
		assertEquals("SHA512withECDSA",Utils.getSignatureAlgorithm(com.kareebo.contacts.thrift.SignatureAlgorithm.SHA512withECDSAprime239v1));
	}
}
