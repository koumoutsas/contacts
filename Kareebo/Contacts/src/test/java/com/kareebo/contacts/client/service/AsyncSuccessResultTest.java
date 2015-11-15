package com.kareebo.contacts.client.service;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link AsyncSuccessResult}.
 */
public class AsyncSuccessResultTest
{
	@Test
	public void test()
	{
		final Long expected=0L;
		final AsyncSuccessResult<Long> result=new AsyncSuccessResult<>(expected);
		assertEquals(expected,result.result());
		assertNull(result.cause());
		assertFalse(result.failed());
		assertTrue(result.succeeded());
	}
}