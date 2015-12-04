package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.Service;

import java.lang.reflect.InvocationTargetException;

/**
 * Service factory for the processor side
 */
public class ServiceDispatcher extends com.kareebo.contacts.client.jobs.ServiceDispatcher
{
	public ServiceDispatcher(final Enqueuers enqueuers)
	{
		super(enqueuers);
	}

	@Override
	public Service constructService(final Class<?> theClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		return (Service)theClass.getDeclaredConstructor().newInstance();
	}
}
