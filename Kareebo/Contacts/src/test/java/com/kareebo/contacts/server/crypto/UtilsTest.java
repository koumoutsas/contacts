package com.kareebo.contacts.server.crypto;

import com.kareebo.contacts.server.gora.Algorithm;
import com.kareebo.contacts.server.gora.CryptoBuffer;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Vector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for crypto utils
 */
public class UtilsTest
{
	final private Vector<byte[]>plaintext=new Vector<>(2);
	private byte[] signature;
	final private CryptoBuffer verificationKey=new CryptoBuffer();
	final static private String ecdsa="ECDSA";

	@Before
	public void setUp() throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g = KeyPairGenerator.getInstance(ecdsa,Utils.getProvider());
		g.initialize(ecSpec, new SecureRandom());
		final KeyPair keyPair=g.generateKeyPair();
		plaintext.add("abc".getBytes());
		plaintext.add("cde".getBytes());
		Signature ecdsaSign = Signature.getInstance("SHA256withECDSA",Utils.getProvider());
		ecdsaSign.initSign(keyPair.getPrivate());
		for(final Object a:plaintext)
		{
			ecdsaSign.update((byte[])a);
		}
		signature = ecdsaSign.sign();
		final ByteBuffer buffer=ByteBuffer.wrap(new X509EncodedKeySpec(
			                                                              keyPair.getPublic().getEncoded
				                                                                                  ())
			                                        .getEncoded());
		buffer.mark();
		verificationKey.setBuffer(buffer);
		verificationKey.setAlgorithm(Algorithm.SHA256withECDSAprime239v1);
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testVerifySignature() throws Exception
	{
		assertTrue(Utils.verifySignature(verificationKey,signature,plaintext));
		final Vector<byte[]> plainTextWrong=new Vector<>(1);
		plainTextWrong.add(plaintext.get(0));
		assertFalse(Utils.verifySignature(verificationKey,signature,plainTextWrong));
		final CryptoBuffer wrongKey=verificationKey;
		wrongKey.setAlgorithm(Algorithm.SHA256);
		exception.expect(NoSuchAlgorithmException.class);
		Utils.verifySignature(wrongKey,signature,plaintext);
	}
}
