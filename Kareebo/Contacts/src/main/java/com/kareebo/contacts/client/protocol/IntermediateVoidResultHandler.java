package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link ResultHandler} for {@link Void} when the result is not final
 */
class IntermediateVoidResultHandler extends ResultHandler<Void>
{
	IntermediateVoidResultHandler(final ErrorEnqueuer enqueuer,final ServiceMethod method)
	{
		super(enqueuer,method);
	}

	@Override
	protected void handleSuccess(@Nonnull final Void result)
	{
	}
}
