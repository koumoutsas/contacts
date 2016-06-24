package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implementation of {@link ResultHandler} for {@link Void} when the result is not final
 */
class IntermediateVoidResultHandler extends ResultHandler<Void>
{
	IntermediateVoidResultHandler(@Nonnull final ErrorEnqueuer enqueuer,@Nonnull final ServiceMethod method)
	{
		super(enqueuer,method);
	}

	@Override
	protected void handleSuccess(@Nullable final Void result)
	{
	}
}
