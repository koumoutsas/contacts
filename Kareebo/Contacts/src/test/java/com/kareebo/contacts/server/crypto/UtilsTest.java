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
import java.util.Vector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Utils}
 */
public class UtilsTest
{
	final static private String ecdsa="ECDSA";
	final private Vector<byte[]> plaintext=new Vector<>(2);
	final private VerificationKey verificationKey=new VerificationKey();
	private ByteBuffer signature;

	@Before
	public void setUp() throws Exception
	{
		// Only for 100% coverage
		new Utils();
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g=KeyPairGenerator.getInstance(ecdsa,Utils.getProvider());
		g.initialize(ecSpec,new SecureRandom());
		final KeyPair keyPair=g.generateKeyPair();
		plaintext.add("abc".getBytes());
		plaintext.add("cde".getBytes());
		Signature ecdsaSign=Signature.getInstance("SHA256withECDSA",Utils.getProvider());
		ecdsaSign.initSign(keyPair.getPrivate());
		for(final Object a : plaintext)
		{
			ecdsaSign.update((byte[])a);
		}
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
		final Vector<byte[]> plainTextWrong=new Vector<>(1);
		plainTextWrong.add(plaintext.get(0));
		signature.rewind();
		assertFalse(Utils.verifySignature(verificationKey,signature,new TestPlaintextSerializer(plainTextWrong)));
		final VerificationKey wrongKey=verificationKey;
		wrongKey.setAlgorithm(SignatureAlgorithm.Fake);
		Utils.verifySignature(wrongKey,signature,plaintextSerializer);
	}
}
