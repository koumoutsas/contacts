package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.ServiceMethod;

/**
 * Implementation of {@link ResultHandler} for {@link Void} when the result is not final
 */
class IntermediateVoidResultHandler extends ResultHandler<Void>
{
	IntermediateVoidResultHandler(final Enqueuer enqueuer,final ServiceMethod method)
	{
		super(enqueuer,method);
	}

	@Override
	void handleSuccess(final Void result)
	{
	}
}
