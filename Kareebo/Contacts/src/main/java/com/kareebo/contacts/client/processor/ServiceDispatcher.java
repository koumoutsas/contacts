package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.Service;

import java.lang.reflect.InvocationTargetException;

/**
 * Service factory for the processor side
 */
public class ServiceDispatcher extends com.kareebo.contacts.client.jobs.ServiceDispatcher
{
	final private PersistedObjectRetriever persistedObjectRetriever;

	public ServiceDispatcher(final PersistedObjectRetriever persistedObjectRetriever,final Enqueuers enqueuers)
	{
		super(enqueuers);
		this.persistedObjectRetriever=persistedObjectRetriever;
	}

	@Override
	public Service constructService(final Class<?> theClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		return (Service)theClass.getDeclaredConstructor(PersistedObjectRetriever.class).newInstance(persistedObjectRetriever);
	}
}
