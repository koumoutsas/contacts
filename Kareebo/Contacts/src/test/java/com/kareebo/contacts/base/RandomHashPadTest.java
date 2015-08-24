package com.kareebo.contacts.base;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link RandomHashPad}
 */
public class RandomHashPadTest
{
	@Test
	public void testGetBytes() throws Exception
	{
		assertEquals(16,new RandomHashPad().getBytes().remaining());
	}
}