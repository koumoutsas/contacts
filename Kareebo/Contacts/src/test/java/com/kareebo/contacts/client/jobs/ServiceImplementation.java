package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link Service} for tests
 */
class ServiceImplementation extends Service
{
	final public static ServiceMethod method=new ServiceMethod(ServiceImplementation.class.getSimpleName(),"1");
	final private Exception error;

	ServiceImplementation()
	{
		this(null);
	}

	ServiceImplementation(final Exception error)
	{
		super(new Context());
		this.error=error;
	}

	@Nonnull
	@Override
	protected ServiceMethod[] methodNames()
	{
		return new ServiceMethod[]{method};
	}

	@Nonnull
	@Override
	protected Functor[] functors()
	{
		return new Functor[]{(payload,enqueuers)->{
			if(error==null)
			{
				enqueuers.intermediateResultEnqueuer(JobType.Protocol).put(new IntermediateJob(JobType.Protocol,method,new Context(),
					                                                                              payload));
			}
			else
			{
				throw error;
			}
		}};
	}
}
