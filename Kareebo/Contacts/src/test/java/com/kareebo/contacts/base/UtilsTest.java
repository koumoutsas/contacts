package com.kareebo.contacts.base;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;

/**
 * Unit test for {@link Utils}
 */
public class UtilsTest
{
	static
	{
		// For 100% coverage
		new Utils();
	}

	@Test
	public void testGetBytes() throws Exception
	{
		final byte[] expected="abc".getBytes();
		assertArrayEquals(expected,Utils.getBytes(ByteBuffer.wrap(expected)));
	}
}