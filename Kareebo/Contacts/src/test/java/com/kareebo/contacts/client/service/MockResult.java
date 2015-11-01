package com.kareebo.contacts.client.service;

import com.kareebo.contacts.thrift.FailedOperation;
import org.vertx.java.core.AsyncResult;

/**
 * Mock result base class
 */
abstract class MockResult<T> implements AsyncResult<T>
{
	private final boolean success;

	/**
	 * @param success Whether the result was successful
	 */
	MockResult(final boolean success)
	{
		this.success=success;
	}

	@Override
	public Throwable cause()
	{
		return success?null:new FailedOperation();
	}

	@Override
	public boolean succeeded()
	{
		return success;
	}

	@Override
	public boolean failed()
	{
		return !succeeded();
	}
}
