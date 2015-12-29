package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Implementation of {@link Service} for tests
 */
public class ServiceImplementation extends Service
{
	final public static ServiceMethod method=new ServiceMethod(ServiceImplementation.class.getSimpleName(),"1");
	final private Exception error;

	ServiceImplementation()
	{
		this(null);
	}

	ServiceImplementation(final Exception error)
	{
		super(null);
		this.error=error;
	}

	@Override
	protected ServiceMethod[] methodNames()
	{
		return new ServiceMethod[]{method};
	}

	@Override
	protected Functor[] functors()
	{
		return new Functor[]{new Functor()
		{
			@Override
			public void run(final TBase payload,final Enqueuers enqueuers) throws Exception
			{
				if(error==null)
				{
					enqueuers.intermediateResultEnqueuer(JobType.Protocol).enqueue(JobType.Protocol,method,null,payload);
				}
				else
				{
					throw error;
				}
			}
		}};
	}
}
