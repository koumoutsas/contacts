package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;

import javax.annotation.Nonnull;

/**
 * Class for jobs that are an error
 */
public class ErrorJob extends Job
{
	@Nonnull
	private final ServiceMethod method;
	@Nonnull
	private final ErrorCode error;
	@Nonnull
	private final Throwable cause;

	/**
	 * Constructor
	 *
	 * @param type   The job type
	 * @param method The service method that caused the error
	 * @param error  The error
	 * @param cause  The cause of the error
	 */
	ErrorJob(@Nonnull final JobType type,@Nonnull final ServiceMethod method,final @Nonnull ErrorCode error,final @Nonnull Throwable cause)
	{
		super(type);
		this.method=method;
		this.error=error;
		this.cause=cause;
	}

	@Nonnull
	public ServiceMethod getMethod()
	{
		return method;
	}

	@Nonnull
	public ErrorCode getError()
	{
		return error;
	}

	@Nonnull
	public Throwable getCause()
	{
		return cause;
	}

	@Override
	void dispatch(@Nonnull final Dispatcher dispatcher) throws Service.ExecutionFailed, ServiceDispatcher.NoSuchService, Service.NoSuchMethod
	{
		dispatcher.dispatch(this);
	}
}
