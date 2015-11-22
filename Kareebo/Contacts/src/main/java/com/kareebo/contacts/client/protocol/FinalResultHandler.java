package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.ServiceMethod;

/**
 * Implementation of {@link ResultHandler} that notifies for success
 */
class FinalResultHandler extends ResultHandler<Void>
{
	FinalResultHandler(final Enqueuer enqueuer,final ServiceMethod method)
	{
		super(enqueuer,method);
	}

	/**
	 * Signal that the result is done. Alias for {@link #handleSuccess(Object)} with parameter null
	 */
	void done()
	{
		handleSuccess(null);
	}

	@Override
	void handleSuccess(final Void result)
	{
		enqueuer.success(method.getServiceName());
	}
}
