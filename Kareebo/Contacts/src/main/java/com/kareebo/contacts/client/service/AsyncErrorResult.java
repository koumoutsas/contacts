package com.kareebo.contacts.client.service;

import org.vertx.java.core.AsyncResult;

/**
 * Implementation of {@link AsyncResult} that returns only error
 */
class AsyncErrorResult<T> implements AsyncResult<T>
{
	final private Throwable cause;

	AsyncErrorResult(final Throwable cause)
	{
		this.cause=cause;
	}

	@Override
	public T result()
	{
		return null;
	}

	@Override
	public Throwable cause()
	{
		return cause;
	}

	@Override
	public boolean succeeded()
	{
		return false;
	}

	@Override
	public boolean failed()
	{
		return true;
	}
}
