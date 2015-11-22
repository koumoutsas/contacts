package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.ServiceMethod;
import org.vertx.java.core.AsyncResult;

/**
 * Implementation of {@link org.vertx.java.core.AsyncResultHandler} using {@link Enqueuer}
 */
abstract class ResultHandler<T> implements org.vertx.java.core.AsyncResultHandler<T>
{
	final protected Enqueuer enqueuer;
	final protected ServiceMethod method;

	ResultHandler(final Enqueuer enqueuer,final ServiceMethod method)
	{
		this.enqueuer=enqueuer;
		this.method=method;
	}

	@Override
	public void handle(final AsyncResult<T> event)
	{
		if(event.failed())
		{
			enqueuer.protocolError(method,event.cause());
		}
		else
		{
			handleSuccess(event.result());
		}
	}

	abstract void handleSuccess(final T result);
}
