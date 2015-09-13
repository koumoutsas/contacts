package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.HashBufferPair;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link HashBufferPairPlaintextSerializer}
 */
public class HashBufferPairPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final HashAlgorithm algorithm=HashAlgorithm.SHA256;
		final ByteBuffer byteBuffer1=ByteBuffer.wrap("abc".getBytes());
		byteBuffer1.mark();
		final HashBuffer hashBuffer1=new HashBuffer(byteBuffer1,algorithm);
		final ByteBuffer byteBuffer2=ByteBuffer.wrap("d".getBytes());
		byteBuffer2.mark();
		final HashBuffer hashBuffer2=new HashBuffer(byteBuffer2,algorithm);
		final Vector<byte[]> plaintext=new HashBufferPairPlaintextSerializer(new HashBufferPair(hashBuffer1,hashBuffer2)).serialize();
		assertEquals(HashBufferPairPlaintextSerializer.LENGTH,plaintext.size());
		final Vector<byte[]> expected=new HashBufferPlaintextSerializer(hashBuffer1).serialize();
		expected.addAll(new HashBufferPlaintextSerializer(hashBuffer2).serialize());
		assertEquals(expected.size(),plaintext.size());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),plaintext.elementAt(i));
		}
	}
}