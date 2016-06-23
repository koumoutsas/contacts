package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.vertx.java.core.AsyncResult;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link org.vertx.java.core.AsyncResultHandler} using {@link ErrorEnqueuer}
 */
abstract public class ResultHandler<T> implements org.vertx.java.core.AsyncResultHandler<T>
{
	final protected ServiceMethod method;
	private final ErrorEnqueuer errorEnqueuer;
	final private JobType jobType;

	protected ResultHandler(final @Nonnull ErrorEnqueuer errorEnqueuer,final @Nonnull ServiceMethod method,final @Nonnull JobType jobType)
	{
		this.errorEnqueuer=errorEnqueuer;
		this.method=method;
		this.jobType=jobType;
	}

	@Override
	public void handle(final @Nonnull AsyncResult<T> event)
	{
		if(event.failed())
		{
			errorEnqueuer.error(new ErrorJob(jobType,method,ErrorCode.Failure,event.cause()));
		}
		else
		{
			handleSuccess(event.result());
		}
	}

	/**
	 * The implementation should issue either a success event in the form of {@link SuccessJob}, or another {@link IntermediateJob}
	 *
	 * @param result The result of the operation
	 */
	abstract protected void handleSuccess(final @Nonnull T result);
}
