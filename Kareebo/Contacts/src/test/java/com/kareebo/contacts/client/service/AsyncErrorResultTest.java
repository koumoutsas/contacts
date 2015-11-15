package com.kareebo.contacts.client.service;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link AsyncErrorResult}
 */
public class AsyncErrorResultTest
{
	@Test
	public void test()
	{
		final Throwable cause=new Exception();
		final AsyncErrorResult<Long> result=new AsyncErrorResult<>(cause);
		assertNull(result.result());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertTrue(result.cause()==cause);
		assertTrue(result.failed());
		assertFalse(result.succeeded());
	}
}