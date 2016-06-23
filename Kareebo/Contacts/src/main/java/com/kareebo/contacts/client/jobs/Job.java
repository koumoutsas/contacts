package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;

import javax.annotation.Nonnull;

/**
 * Base class for all job types
 */
public abstract class Job
{
	private final JobType type;

	Job(final @Nonnull JobType type)
	{
		this.type=type;
	}

	@Nonnull
	JobType getType()
	{
		return type;
	}

	/**
	 * Visitor pattern for dispatching a {@link Job}
	 *
	 * @param dispatcher The dispatcher
	 * @throws Service.ExecutionFailed
	 * @throws ServiceDispatcher.NoSuchService
	 * @throws Service.NoSuchMethod
	 */
	abstract void dispatch(final @Nonnull Dispatcher dispatcher) throws Service.ExecutionFailed, ServiceDispatcher.NoSuchService, Service.NoSuchMethod;
}
