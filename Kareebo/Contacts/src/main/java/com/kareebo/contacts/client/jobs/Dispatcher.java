package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;

import javax.annotation.Nonnull;

/**
 * Combines a {@link ServiceDispatcher} for dispatching {@link Job}s to {@link Service}s and a {@link FinalResultDispatcher} to dispatch error or
 * success events. To be used to construct {@link Runner} by mapping each {@link JobType} to a {@link Dispatcher}
 */
public class Dispatcher
{
	@Nonnull
	private final ServiceDispatcher serviceDispatcher;
	@Nonnull
	private final FinalResultDispatcher finalResultDispatcher;

	public Dispatcher(final @Nonnull ServiceDispatcher serviceDispatcher,final @Nonnull FinalResultDispatcher finalResultDispatcher)
	{
		this.serviceDispatcher=serviceDispatcher;
		this.finalResultDispatcher=finalResultDispatcher;
	}

	void dispatch(final @Nonnull IntermediateJob job) throws Service.ExecutionFailed, ServiceDispatcher.NoSuchService, Service.NoSuchMethod
	{
		serviceDispatcher.run(job.getMethod(),job.getPayload(),job.getContext());
	}

	void dispatch(final @Nonnull ErrorJob job)
	{
		finalResultDispatcher.dispatch(job);
	}

	void dispatch(final @Nonnull SuccessJob job)
	{
		finalResultDispatcher.dispatch(job);
	}
}
