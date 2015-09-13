package com.kareebo.contacts.server.crypto;

import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.server.handler.TestPlaintextSerializer;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

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
	final private VerificationKey verificationKey=new VerificationKey();
	private ByteBuffer signature;

	@Before
	public void setUp() throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g=KeyPairGenerator.getInstance(ecdsa,Utils.getProvider());
		g.initialize(ecSpec,new SecureRandom());
		final KeyPair keyPair=g.generateKeyPair();
		Signature ecdsaSign=Signature.getInstance("SHA256withECDSA",Utils.getProvider());
		ecdsaSign.initSign(keyPair.getPrivate());
		ecdsaSign.update(plaintext);
		signature=ByteBuffer.wrap(ecdsaSign.sign());
		signature.mark();
		final ByteBuffer buffer=ByteBuffer.wrap(new X509EncodedKeySpec(
			                                                              keyPair.getPublic().getEncoded
				                                                                                  ())
			                                        .getEncoded());
		buffer.mark();
		verificationKey.setBuffer(buffer);
		verificationKey.setAlgorithm(SignatureAlgorithm.SHA256withECDSAprime239v1);
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testVerifySignature() throws Exception
	{
		signature.rewind();
		final TestPlaintextSerializer plaintextSerializer=new TestPlaintextSerializer(plaintext);
		assertTrue(Utils.verifySignature(verificationKey,signature,plaintextSerializer));
		final byte[] plainTextWrong="def".getBytes();
		signature.rewind();
		assertFalse(Utils.verifySignature(verificationKey,signature,new TestPlaintextSerializer(plainTextWrong)));
		final VerificationKey wrongKey=verificationKey;
		wrongKey.setAlgorithm(SignatureAlgorithm.Fake);
		Utils.verifySignature(wrongKey,signature,plaintextSerializer);
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
}
