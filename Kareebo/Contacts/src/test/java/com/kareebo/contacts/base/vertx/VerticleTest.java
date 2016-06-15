package com.kareebo.contacts.base.vertx;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Unit test for {@link Verticle}
 */
public class VerticleTest
{
	@Test
	public void getInjector() throws Exception
	{
		final Verticle verticle=new Verticle()
		{
			@Nonnull
			@Override
			protected AbstractModule provideModule()
			{
				return new AbstractModule()
				{
					@Override
					protected void configure()
					{
					}
				};
			}
		};
		final Injector injector=verticle.getInjector();
		assertNotNull(injector);
		assertSame(injector,verticle.getInjector());
	}

}