package com.kareebo.contacts.base;

import com.kareebo.contacts.common.Algorithm;
import com.kareebo.contacts.common.CryptoBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link CryptoBufferPlaintextSerializer}
 */
public class CryptoBufferPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final Algorithm algorithm=Algorithm.SHA256;
		final byte[] bytes="abc".getBytes();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
		byteBuffer.mark();
		final Vector<byte[]> plaintext=new CryptoBufferPlaintextSerializer(new CryptoBuffer(byteBuffer,algorithm)).serialize();
		assertEquals(AlgorithmPlaintextSerializer.LENGTH+1,plaintext.size());
		assertArrayEquals(new AlgorithmPlaintextSerializer(algorithm).serialize().elementAt(0),plaintext
			                                                                                       .elementAt(0));
		assertArrayEquals(bytes,plaintext.elementAt(1));
	}
}