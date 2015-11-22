package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Implementation of {@link ResultHandler} that spawns a job for the processor side
 */
class IntermediateResultHandler<T extends TBase> extends ResultHandler<T>
{
	IntermediateResultHandler(final Enqueuer enqueuer,final ServiceMethod method)
	{
		super(enqueuer,method);
	}

	@Override
	void handleSuccess(final T result)
	{
		enqueuer.processor(method,result);
	}
}
