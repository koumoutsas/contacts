package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.Service;
import com.kareebo.contacts.client.persistentStorage.PersistedObjectRetriever;
import com.kareebo.contacts.thrift.client.jobs.Context;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Service factory for the processor side
 */
public class ServiceDispatcher extends com.kareebo.contacts.client.jobs.ServiceDispatcher
{
	final private PersistedObjectRetriever persistedObjectRetriever;

	/**
	 * Create a service dispatcher
	 *
	 * @param persistedObjectRetriever Used to access the persistent storage
	 * @param enqueuers                The dispatcher enqueuers
	 */
	public ServiceDispatcher(final @Nonnull PersistedObjectRetriever persistedObjectRetriever,final @Nonnull Enqueuers enqueuers)
	{
		super(enqueuers);
		this.persistedObjectRetriever=persistedObjectRetriever;
	}

	@Override
	protected Service constructService(@Nonnull final Class<?> service,final Context context) throws NoSuchMethodException, IllegalAccessException,
		                                                                                                 InvocationTargetException, InstantiationException
	{
		return (Service)service.getDeclaredConstructor(PersistedObjectRetriever.class).newInstance(persistedObjectRetriever);
	}
}
