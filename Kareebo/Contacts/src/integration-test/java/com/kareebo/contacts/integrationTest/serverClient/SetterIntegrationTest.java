package com.kareebo.contacts.integrationTest.serverClient;

import org.apache.thrift.TBase;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Setter integration test base
 */
abstract class SetterIntegrationTest<T extends TBase> extends SingleOperationIntegrationTest<T>
{
	@Override
	@Nonnull
	protected Supplier<T> expectedStateSupplier()
	{
		return this::payload;
	}
}
