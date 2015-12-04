package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Implementation of {@link ResultHandler} that spawns a job for the processor side
 */
class IntermediateResultHandler<T extends TBase> extends ResultHandler<T>
{
	final private IntermediateResultEnqueuer intermediateResultEnqueuer;

	IntermediateResultHandler(final IntermediateResultEnqueuer intermediateResultEnqueuer,final ErrorEnqueuer errorEnqueuer,final ServiceMethod method)
	{
		super(errorEnqueuer,method);
		this.intermediateResultEnqueuer=intermediateResultEnqueuer;
	}

	@Override
	protected void handleSuccess(final T result)
	{
		intermediateResultEnqueuer.enqueue(JobType.Protocol,method,result);
	}
}
