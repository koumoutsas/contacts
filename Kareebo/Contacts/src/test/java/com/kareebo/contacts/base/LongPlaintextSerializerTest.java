package com.kareebo.contacts.base;

import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link LongPlaintextSerializer}
 */
public class LongPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final Long l=(long)15;
		final Vector<byte[]> plaintext=new LongPlaintextSerializer(l).serialize();
		final Vector<byte[]> expected=new StringPlaintextSerializer(String.valueOf(l)).serialize();
		assertEquals(expected.size(),plaintext.size());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),plaintext.elementAt(i));
		}
	}
}