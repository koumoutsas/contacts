package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link PublicKeysPlaintextSerializer}
 */
public class PublicKeysPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final byte[] bytes="abc".getBytes();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
		byteBuffer.mark();
		final EncryptionKey encryptionKey=new EncryptionKey(byteBuffer,EncryptionAlgorithm.RSA2048);
		final VerificationKey verificationKey=new VerificationKey(byteBuffer,SignatureAlgorithm
			                                                                     .SHA256withECDSAprime239v1);
		final PublicKeys publicKeys=new PublicKeys(encryptionKey,verificationKey);
		final Vector<byte[]> plaintext=new PublicKeysPlaintextSerializer(publicKeys).serialize();
		assertEquals(EncryptionKeyPlaintextSerializer.LENGTH+VerificationKeyPlaintextSerializer.LENGTH,plaintext
			                                                                                               .size());
		final Vector<byte[]> expected=new EncryptionKeyPlaintextSerializer(encryptionKey).serialize();
		expected.addAll(new VerificationKeyPlaintextSerializer(verificationKey).serialize());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),plaintext.elementAt(i));
		}
	}
}