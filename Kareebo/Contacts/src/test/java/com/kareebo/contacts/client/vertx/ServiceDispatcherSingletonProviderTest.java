package com.kareebo.contacts.client.vertx;

import com.kareebo.contacts.client.protocol.Enqueuers;
import com.kareebo.contacts.client.protocol.ServiceDispatcher;
import com.kareebo.contacts.crypto.TestKeyPair;
import com.kareebo.contacts.thrift.ClientId;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

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
		final ServiceDispatcher dispatcher=ServiceDispatcherSingletonProvider.get(new Enqueuers(new Implementations
			                                                                                            .TestIntermediateResultEnqueuer
			                                                                                        (),new Implementations
				                                                                                               .TestFinalResultEnqueuer()),new TestKeyPair().signingKey(),new ClientId());
		assertNotNull(dispatcher);
		assertSame(dispatcher,ServiceDispatcherSingletonProvider.get());
		try
		{
			ServiceDispatcherSingletonProvider.get(new Enqueuers(new Implementations.TestIntermediateResultEnqueuer(),new
				                                                                                                          Implementations.TestFinalResultEnqueuer()),new TestKeyPair().signingKey(),new ClientId());
			fail();
		}
		catch(ServiceDispatcherSingletonProvider.DuplicateInitialization ignored)
		{
		}
		ServiceDispatcherSingletonProvider.reset();
		ServiceDispatcherSingletonProvider.get(new Enqueuers(new Implementations.TestIntermediateResultEnqueuer(),new Implementations
			                                                                                                              .TestFinalResultEnqueuer()),new TestKeyPair().signingKey(),new ClientId());
	}

	@After
	public void tearDown()
	{
		ServiceDispatcherSingletonProvider.reset();
	}
}