package com.kareebo.contacts.client.jobs;

import javax.annotation.Nonnull;

/**
 * Base class providing a {@link Dispatcher} implementation for all tests that use it
 */
abstract class DispatcherTestBase
{
	final EnqueuerImplementation enqueuerImplementation=new EnqueuerImplementation();
	final FinalResultDispatcher finalResultDispatcher=new FinalResultDispatcher()
	{
		@Override
		public void dispatch(@Nonnull final ErrorJob errorJob)
		{
			enqueuerImplementation.error(errorJob);
		}

		@Override
		public void dispatch(@Nonnull final SuccessJob successJob)
		{
			enqueuerImplementation.success(successJob);
		}
	};
	final Dispatcher dispatcher=new Dispatcher(new ServiceDispatcherImplementation(enqueuerImplementation),finalResultDispatcher);
}
