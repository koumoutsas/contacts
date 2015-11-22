package com.kareebo.contacts.client.jobs;

import java.lang.reflect.InvocationTargetException;

/**
 * Implementation of {@link ServiceDispatcher} for tests
 */
public class ServiceDispatcherImplementation extends ServiceDispatcher
{
	final private Exception error;

	public ServiceDispatcherImplementation(final EnqueuerImplementation enqueuer)
	{
		this(enqueuer,null);
	}

	public ServiceDispatcherImplementation(final EnqueuerImplementation enqueuer,final Exception error)
	{
		super(enqueuer);
		this.error=error;
	}

	@Override
	public Service constructService(final Class<?> theClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
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
