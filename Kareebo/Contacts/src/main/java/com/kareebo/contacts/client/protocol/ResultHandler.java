package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.vertx.java.core.AsyncResult;

/**
 * Implementation of {@link org.vertx.java.core.AsyncResultHandler} using {@link ErrorEnqueuer}
 */
abstract class ResultHandler<T> implements org.vertx.java.core.AsyncResultHandler<T>
{
	final protected ErrorEnqueuer errorEnqueuer;
	final protected ServiceMethod method;

	ResultHandler(final ErrorEnqueuer errorEnqueuer,final ServiceMethod method)
	{
		this.errorEnqueuer=errorEnqueuer;
		this.method=method;
	}

	@Override
	public void handle(final AsyncResult<T> event)
	{
		if(event.failed())
		{
			errorEnqueuer.error(JobType.Protocol,method,ErrorCode.Failure);
		}
		else
		{
			handleSuccess(event.result());
		}
	}

	abstract protected void handleSuccess(final T result);
}
