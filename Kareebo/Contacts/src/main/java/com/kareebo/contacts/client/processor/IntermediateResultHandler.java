package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.apache.thrift.TBase;

/**
 * Implementation of {@link ResultHandler} that spawns a job for the processor side
 */
class IntermediateResultHandler<T extends TBase> extends ResultHandler<T>
{
	final private IntermediateResultEnqueuer intermediateResultEnqueuer;
	final private com.kareebo.contacts.client.protocol.ServiceMethod jobMethod;
	final private Context context;

	IntermediateResultHandler(final IntermediateResultEnqueuer intermediateResultEnqueuer,final com.kareebo.contacts.client.protocol
		                                                                                            .ServiceMethod jobMethod,final ErrorEnqueuer errorEnqueuer,final ServiceMethod callerMethod,final Context context)
	{
		super(errorEnqueuer,callerMethod);
		this.intermediateResultEnqueuer=intermediateResultEnqueuer;
		this.jobMethod=jobMethod;
		this.context=context;
	}

	@Override
	protected void handleSuccess(final T result)
	{
		intermediateResultEnqueuer.enqueue(JobType.Protocol,jobMethod,context,result);
	}
}
