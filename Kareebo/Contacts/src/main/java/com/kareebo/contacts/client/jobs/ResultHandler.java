package com.kareebo.contacts.client.jobs;

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
	final private JobType jobType;
	
	ResultHandler(final ErrorEnqueuer errorEnqueuer,final ServiceMethod method,final JobType jobType)
	{
		this.errorEnqueuer=errorEnqueuer;
		this.method=method;
		this.jobType=jobType;
	}

	@Override
	public void handle(final AsyncResult<T> event)
	{
		if(event.failed())
		{
			errorEnqueuer.error(jobType,method,ErrorCode.Failure);
		}
		else
		{
			handleSuccess(event.result());
		}
	}

	abstract protected void handleSuccess(final T result);
}
