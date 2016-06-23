package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

import javax.annotation.Nonnull;

/**
 * Class for jobs that are not an error or a final result
 */
public class IntermediateJob extends Job
{
	private final ServiceMethod method;
	private final Context context;
	private final TBase payload;

	/**
	 * @param type    The job type
	 * @param method  The service method that caused the error
	 * @param context The service context
	 * @param payload The payload of the operation
	 */
	public IntermediateJob(final @Nonnull JobType type,final @Nonnull ServiceMethod method,final @Nonnull Context context,final @Nonnull TBase
		                                                                                                                      payload)
	{
		super(type);
		this.method=method;
		this.context=context;
		this.payload=payload;
	}

	@Nonnull
	public ServiceMethod getMethod()
	{
		return method;
	}

	@Nonnull
	public Context getContext()
	{
		return context;
	}

	@Nonnull
	public TBase getPayload()
	{
		return payload;
	}

	@Override
	void dispatch(@Nonnull final Dispatcher dispatcher) throws Service.ExecutionFailed, ServiceDispatcher.NoSuchService, Service.NoSuchMethod
	{
		dispatcher.dispatch(this);
	}
}
