package com.kareebo.contacts.client.service;

import org.vertx.java.core.AsyncResult;

/**
 * Implementation of {@link AsyncResult} that returns only success
 */
class AsyncSuccessResult<T> implements AsyncResult<T>
{
	final private T result;

	AsyncSuccessResult(final T result)
	{
		this.result=result;
	}

	@Override
	public T result()
	{
		return result;
	}

	@Override
	public Throwable cause()
	{
		return null;
	}

	@Override
	public boolean succeeded()
	{
		return true;
	}

	@Override
	public boolean failed()
	{
		return false;
	}
}
