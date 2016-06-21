package com.kareebo.contacts.integrationTest.serverClient;

import org.apache.thrift.TBase;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Setter integration test base
 */
abstract class SetterIntegrationTest<T extends TBase> extends SingleOperationIntegrationTest<T>
{
	@Nonnull
	@Override
	protected Supplier<T> stateRetriever()
	{
		return ()->{
			try
			{
				return stateRetrieverImplementation().get();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e.getMessage());
			}
		};
	}

	@Override
	@Nonnull
	protected Supplier<T> expectedStateSupplier()
	{
		return this::payload;
	}

	@Nonnull
	abstract protected ThrowingSupplier<T> stateRetrieverImplementation();

	@FunctionalInterface
	interface ThrowingSupplier<S extends TBase>
	{
		@Nonnull
		S get() throws Exception;
	}

}
