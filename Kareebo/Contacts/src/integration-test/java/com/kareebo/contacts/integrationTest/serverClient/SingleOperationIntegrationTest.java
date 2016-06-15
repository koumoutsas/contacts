package com.kareebo.contacts.integrationTest.serverClient;

import com.kareebo.contacts.client.persistentStorage.PersistentStorage;
import com.kareebo.contacts.client.vertx.ServiceDispatcherSingletonProvider;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Integration test base for single operations, like getters and setters
 */
abstract class SingleOperationIntegrationTest<T extends TBase> extends IntegrationTestBase
{
	@Nonnull
	@Override
	protected String serviceName()
	{
		return serviceMethod().getServiceName();
	}

	@Override
	protected void testMethod() throws Exception
	{
		final T payload=payload();
		assertNotEquals(payload,stateRetriever().get());
		ServiceDispatcherSingletonProvider.get().run(serviceMethod(),payload,null);
	}

	@Nonnull
	abstract protected T payload();

	@Nonnull
	abstract protected Supplier<T> stateRetriever();

	@Override
	protected void checkDatastore() throws TException, PersistentStorage.NoSuchKey
	{
		assertEquals(expectedStateSupplier().get(),stateRetriever().get());
	}

	@Nonnull
	abstract protected Supplier<T> expectedStateSupplier();

	@Nonnull
	abstract com.kareebo.contacts.client.protocol.ServiceMethod serviceMethod();
}
