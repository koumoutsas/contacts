package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.persistentStorage.PersistedObjectRetriever;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ServiceDispatcher}
 */
public class ServiceDispatcherTest
{
	@Test
	public void testConstructService() throws Exception
	{
		assertEquals(ServiceImplementation.class,new ServiceDispatcher(null,null).constructService(ServiceImplementation.class,null).getClass
			                                                                                                                             ());
	}

	private static class ServiceImplementation extends Service
	{
		ServiceImplementation(final PersistedObjectRetriever persistedObjectRetriever)
		{
			super(null,persistedObjectRetriever);
		}

		@Override
		protected ServiceMethod[] methodNames()
		{
			return new ServiceMethod[0];
		}

		@Override
		protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
		{
			return new com.kareebo.contacts.client.jobs.Service.Functor[0];
		}
	}
}