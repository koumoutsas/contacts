package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Implementation of {@link ServiceDispatcher} for tests
 */
class ServiceDispatcherImplementation extends ServiceDispatcher
{
	static final JobType jobType=JobType.Protocol;
	final private Exception error;

	ServiceDispatcherImplementation(final EnqueuerImplementation enqueuer)
	{
		this(enqueuer,null);
	}
	ServiceDispatcherImplementation(final EnqueuerImplementation enqueuer,final Exception error)
	{
		super(new Enqueuers(jobType,enqueuer,enqueuer));
		this.error=error;
	}

	@Override
	protected Service constructService(@Nonnull final Class<?> service,final Context context) throws NoSuchMethodException,
		                                                                                                 IllegalAccessException,
		                                                                                                 InvocationTargetException, InstantiationException
	{
		if(error instanceof IllegalAccessException)
		{
			throw (IllegalAccessException)error;
		}
		if(error instanceof InvocationTargetException)
		{
			throw (InvocationTargetException)error;
		}
		if(error instanceof InstantiationException)
		{
			throw (InstantiationException)error;
		}
		return new ServiceImplementation(error);
	}
}
