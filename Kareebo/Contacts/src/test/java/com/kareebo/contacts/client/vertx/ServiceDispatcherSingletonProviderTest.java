package com.kareebo.contacts.client.vertx;

import com.kareebo.contacts.client.protocol.Enqueuers;
import com.kareebo.contacts.client.protocol.ServiceDispatcher;
import com.kareebo.contacts.thrift.ClientId;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Unit test for {@link ServiceDispatcherSingletonProvider}
 */
public class ServiceDispatcherSingletonProviderTest
{
	@Test
	public void get() throws Exception
	{
		// For 100% coverage
		new ServiceDispatcherSingletonProvider();
		try
		{
			ServiceDispatcherSingletonProvider.get();
			fail();
		}
		catch(ServiceDispatcherSingletonProvider.Uninitialized ignored)
		{
		}
		//noinspection ConstantConditions
		final ServiceDispatcher dispatcher=ServiceDispatcherSingletonProvider.get(new Enqueuers(new Implementations.TestIntermediateResultEnqueuer(),new Implementations.TestFinalResultEnqueuer()),null,new ClientId());
		assertNotNull(dispatcher);
		assert (dispatcher==ServiceDispatcherSingletonProvider.get());
		try
		{
			//noinspection ConstantConditions
			ServiceDispatcherSingletonProvider.get(new Enqueuers(new Implementations.TestIntermediateResultEnqueuer(),new Implementations.TestFinalResultEnqueuer()),null,new ClientId());
			fail();
		}
		catch(ServiceDispatcherSingletonProvider.DuplicateInitialization ignored)
		{
		}
		ServiceDispatcherSingletonProvider.reset();
		//noinspection ConstantConditions
		ServiceDispatcherSingletonProvider.get(new Enqueuers(new Implementations.TestIntermediateResultEnqueuer(),new Implementations.TestFinalResultEnqueuer()),null,new ClientId());
	}

	@After
	public void tearDown()
	{
		ServiceDispatcherSingletonProvider.reset();
	}
}