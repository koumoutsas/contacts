package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.persistentStorage.PersistedObjectRetriever;
import com.kareebo.contacts.client.persistentStorage.PersistentStorage;
import com.kareebo.contacts.thrift.client.jobs.*;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ServiceDispatcher}
 */
public class ServiceDispatcherTest
{
	@Test
	public void testConstructService() throws Exception
	{
		assertEquals(ServiceImplementation.class,new ServiceDispatcher(new PersistedObjectRetriever(new PersistentStorage()
		{
			@Nonnull
			@Override
			public byte[] get(@Nonnull final String key) throws NoSuchKey
			{
				return new byte[0];
			}

			@Override
			public void put(@Nonnull final String key,@Nonnull final byte[] value)
			{
			}

			@Override
			public void remove(@Nonnull final String key) throws NoSuchKey
			{
			}

			@Override
			public void start()
			{
			}

			@Override
			public void commit()
			{
			}

			@Override
			public void rollback()
			{
			}
		}),new Enqueuers(new HashMap<>(),new FinalResultEnqueuer()
		{
			@Override
			public void success(@Nonnull final JobType type,@Nonnull final String service,final SuccessCode result)
			{
			}

			@Override
			public void error(@Nonnull final JobType type,final ServiceMethod method,@Nonnull final ErrorCode error)
			{
			}
		})).constructService
			    (ServiceImplementation.class,null)
			                                         .getClass());
	}

	private static class ServiceImplementation extends Service
	{
		ServiceImplementation(final PersistedObjectRetriever persistedObjectRetriever)
		{
			super(new Context(),persistedObjectRetriever);
		}

		@Nonnull
		@Override
		protected ServiceMethod[] methodNames()
		{
			return new ServiceMethod[0];
		}

		@Nonnull
		@Override
		protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
		{
			return new com.kareebo.contacts.client.jobs.Service.Functor[0];
		}
	}
}