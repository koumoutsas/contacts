package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;

/**
 * Implementation of {@link ResultHandler} that notifies for success
 */
class FinalResultHandler extends ResultHandler<Void>
{
	final private FinalResultEnqueuer enqueuer;

	FinalResultHandler(final FinalResultEnqueuer enqueuer,final ServiceMethod method)
	{
		super(enqueuer,method);
		this.enqueuer=enqueuer;
	}

	@Override
	protected void handleSuccess(final Void result)
	{
		enqueuer.success(JobType.Protocol,method.getServiceName(),SuccessCode.Ok);
	}
}