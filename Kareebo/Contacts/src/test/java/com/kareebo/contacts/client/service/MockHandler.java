package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.ResultHandler;
import com.kareebo.contacts.thrift.FailedOperation;

import static org.junit.Assert.assertTrue;

/**
 * Test {@link ResultHandler}
 */
class MockHandler<T> implements ResultHandler<T>
{
	boolean succeeded;

	@Override
	public void handleError(final Throwable cause)
	{
		succeeded=false;
		assertTrue(cause instanceof FailedOperation);
	}

	@Override
	public void handle(final T event)
	{
		succeeded=true;
	}
}
