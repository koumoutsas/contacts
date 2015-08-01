package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link EncryptionKeyPlaintextSerializer}
 */
public class EncryptionKeyPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final EncryptionAlgorithm algorithm=EncryptionAlgorithm.RSA2048;
		final byte[] bytes="abc".getBytes();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
		byteBuffer.mark();
		final Vector<byte[]> plaintext=new EncryptionKeyPlaintextSerializer(new EncryptionKey(byteBuffer,
			                                                                                  algorithm)).serialize();
		assertEquals(EnumPlaintextSerializer.LENGTH+1,plaintext.size());
		assertArrayEquals(new EnumPlaintextSerializer<>(algorithm).serialize().elementAt(0),plaintext
			                                                                                       .elementAt(0));
		assertArrayEquals(bytes,plaintext.elementAt(1));
	}
}