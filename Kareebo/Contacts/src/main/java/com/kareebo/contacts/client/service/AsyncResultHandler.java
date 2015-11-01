package com.kareebo.contacts.client.service;

import org.vertx.java.core.AsyncResult;

/**
 * Wrapper around {@link org.vertx.java.core.AsyncResultHandler} that calls a {@link ResultHandler}
 */
class AsyncResultHandler<T> implements org.vertx.java.core.AsyncResultHandler<T>
{
	private final ResultHandler<T> handler;

	public AsyncResultHandler(final ResultHandler<T> handler)
	{
		this.handler=handler;
	}

	@Override
	public void handle(final AsyncResult<T> event)
	{
		if(event.succeeded())
		{
			handler.handle(event.result());
		}
		else
		{
			handler.handleError(event.cause());
		}
	}
}
