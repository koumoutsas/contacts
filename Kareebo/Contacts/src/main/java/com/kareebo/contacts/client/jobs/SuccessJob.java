package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;

import javax.annotation.Nonnull;

/**
 * Class for jobs for successful completions of a service execution
 */
public class SuccessJob extends Job
{
	final private String service;
	final private SuccessCode result;

	public SuccessJob(@Nonnull final JobType type,final @Nonnull String service,final @Nonnull SuccessCode result)
	{
		super(type);
		this.service=service;
		this.result=result;
	}

	@Nonnull
	public String getService()
	{
		return service;
	}

	@Nonnull
	public SuccessCode getResult()
	{
		return result;
	}

	@Override
	void dispatch(@Nonnull final Dispatcher dispatcher) throws Service.ExecutionFailed, ServiceDispatcher.NoSuchService, Service.NoSuchMethod
	{
		dispatcher.dispatch(this);
	}
}
