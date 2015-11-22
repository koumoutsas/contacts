package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.client.jobs.Service;

import java.lang.reflect.InvocationTargetException;

/**
 * Service factory for the processor side
 */
public class ServiceDispatcher extends com.kareebo.contacts.client.jobs.ServiceDispatcher
{
	public ServiceDispatcher(final Enqueuer enqueuer)
	{
		super(enqueuer);
	}

	@Override
	public Service constructService(final Class<?> theClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		return (Service)theClass.getDeclaredConstructor().newInstance();
	}
}
