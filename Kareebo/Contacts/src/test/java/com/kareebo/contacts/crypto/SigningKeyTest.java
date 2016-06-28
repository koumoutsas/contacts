package com.kareebo.contacts.crypto;

import com.kareebo.contacts.thrift.SignatureAlgorithm;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link SigningKey}
 */
public class SigningKeyTest
{
	@Test
	public void test() throws Exception
	{
		final PrivateKey expectedKey=new TestSignatureKeyPair().getPrivate();
		final SignatureAlgorithm expectedAlgorithm=SignatureAlgorithm.Fake;
		final ByteBuffer buffer=ByteBuffer.wrap(new PKCS8EncodedKeySpec(expectedKey.getEncoded()).getEncoded());
		buffer.mark();
		final SigningKey signingKey=new SigningKey(new com.kareebo.contacts.thrift.client.SigningKey(buffer,expectedAlgorithm));
		assertEquals(expectedAlgorithm,signingKey.algorithm);
		assertEquals(expectedKey,signingKey.key);
	}
}
