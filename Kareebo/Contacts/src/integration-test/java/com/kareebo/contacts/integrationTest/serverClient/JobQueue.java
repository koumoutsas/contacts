package com.kareebo.contacts.integrationTest.serverClient;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kareebo.contacts.client.jobs.*;
import com.kareebo.contacts.client.persistentStorage.PersistedObjectRetriever;
import com.kareebo.contacts.client.persistentStorage.PersistentStorage;
import com.kareebo.contacts.client.processor.ServiceDispatcher;
import com.kareebo.contacts.thrift.client.jobs.JobType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;

/**
 * Simple job pipe
 */
@Singleton
class JobQueue implements Dequeuer, IntermediateResultEnqueuer, FinalResultEnqueuer, FinalResultDispatcher
{
	boolean done;
	Throwable error;
	private IntermediateJob job;
	private Runner runner;

	void setRunner(@Nonnull final Runner runner)
	{
		this.runner=runner;
	}

	@Override
	public Job get()
	{
		final IntermediateJob next=job;
		job=null;
		return next;
	}

	@Override
	public void put(@Nonnull final IntermediateJob job)
	{
		assertNull(this.job);
		this.job=job;
		runner.run();
	}

	@Override
	public void error(@Nonnull final ErrorJob job)
	{
		done=true;
		this.error=error.getCause();
	}

	@Override
	public void success(@Nonnull final SuccessJob job)
	{
		done=true;
	}

	@Nonnull
	Map<JobType,Dispatcher> createDispatchers(@Nonnull final Injector injector)
	{
		final Map<JobType,Dispatcher> ret=new HashMap<>(2);
		ret.put(JobType.Processor,new Dispatcher(new ServiceDispatcher(new PersistedObjectRetriever(injector.getInstance(PersistentStorage
			                                                                                                                 .class)),new Enqueuers
				                                                                                                                          (JobType
					                                                                                                                           .Protocol,this,this)),
			                                        this));
		return ret;
	}

	@Override
	public void dispatch(@Nonnull final ErrorJob errorJob)
	{
	}

	@Override
	public void dispatch(@Nonnull final SuccessJob successJob)
	{
	}
}
