package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link SetHashBufferPlaintextSerializer}
 */
public class SetHashBufferPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final Set<HashBuffer> set=new HashSet<>();
		final ByteBuffer b1=ByteBuffer.wrap("1".getBytes());
		b1.mark();
		set.add(new HashBuffer(b1,HashAlgorithm.SHA256));
		final ByteBuffer b2=ByteBuffer.wrap("2".getBytes());
		b2.mark();
		set.add(new HashBuffer(b2,HashAlgorithm.SHA256));
		final SetHashBufferPlaintextSerializer setHashBufferPlaintextSerializer=new SetHashBufferPlaintextSerializer(set);
		final Vector<byte[]> result=setHashBufferPlaintextSerializer.serialize();
		final int expectedSize=set.size()*HashBufferPlaintextSerializer.LENGTH;
		assertEquals(expectedSize,result.size());
		final Vector<byte[]> expected=new Vector<>(expectedSize);
		for(final HashBuffer h : set)
		{
			expected.addAll(new HashBufferPlaintextSerializer(h).serialize());
		}
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),result.elementAt(i));
		}
	}
}