package com.kareebo.contacts.base;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link RandomHashPad}
 */
public class RandomHashPadTest
{
	@Test
	public void testGetBytes() throws Exception
	{
		final RandomHashPad randomHashPad=new RandomHashPad();
		final byte[] bytes=randomHashPad.getBytes();
		assertEquals(16,bytes.length);
		final ByteBuffer byteBuffer=randomHashPad.getByteBuffer();
		assertEquals(bytes.length,byteBuffer.remaining());
		assertArrayEquals(bytes,Utils.getBytes(byteBuffer));
	}
}