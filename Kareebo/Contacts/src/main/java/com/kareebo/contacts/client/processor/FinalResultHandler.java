package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.SuccessJob;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implementation of {@link ResultHandler} that notifies for success
 */
class FinalResultHandler extends ResultHandler<Void>
{
	final private FinalResultEnqueuer enqueuer;

	FinalResultHandler(final @Nonnull FinalResultEnqueuer enqueuer,final @Nonnull ServiceMethod method)
	{
		super(enqueuer,method);
		this.enqueuer=enqueuer;
	}

	@Override
	protected void handleSuccess(final @Nullable Void result)
	{
		enqueuer.success(new SuccessJob(JobType.Processor,method.getServiceName(),SuccessCode.Ok));
	}
}
