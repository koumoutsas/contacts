package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateJob;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.apache.thrift.TBase;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link ResultHandler} that spawns a job for the processor side
 */
class IntermediateResultHandler<T extends TBase> extends ResultHandler<T>
{
	final private IntermediateResultEnqueuer intermediateResultEnqueuer;
	final private com.kareebo.contacts.client.processor.ServiceMethod jobMethod;
	final private Context context;

	IntermediateResultHandler(final @Nonnull IntermediateResultEnqueuer intermediateResultEnqueuer,final @Nonnull com.kareebo.contacts.client.processor
		                                                                                                              .ServiceMethod jobMethod,final
	                          @Nonnull ErrorEnqueuer errorEnqueuer,final @Nonnull ServiceMethod callerMethod,final Context context)
	{
		super(errorEnqueuer,callerMethod);
		this.intermediateResultEnqueuer=intermediateResultEnqueuer;
		this.jobMethod=jobMethod;
		this.context=context;
	}

	@Override
	protected void handleSuccess(@Nonnull final T result)
	{
		intermediateResultEnqueuer.put(new IntermediateJob(JobType.Processor,jobMethod,context,result));
	}
}
