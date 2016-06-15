package com.kareebo.contacts.base.vertx;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.annotation.Nonnull;

/**
 * Base class for extensions of {@link org.vertx.java.platform.Verticle} with {@link com.google.inject.Injector} capabilities
 */
abstract public class Verticle extends org.vertx.java.platform.Verticle
{
	private final Injector injector=Guice.createInjector(provideModule());

	/**
	 * Get the {@link Injector} of the {@link com.kareebo.contacts.client.vertx.Verticle} in order to be able to have the same injection context
	 *
	 * @return {@link #injector}
	 */
	@Nonnull
	public Injector getInjector()
	{
		return injector;
	}

	/**
	 * @return An {@link AbstractModule} that binds all necessary implementations
	 */
	@Nonnull
	protected abstract AbstractModule provideModule();
}
