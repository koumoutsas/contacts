package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Implementation of {@link Service} for tests
 */
public class ServiceImplementation extends Service
{
	final private Exception error;

	ServiceImplementation()
	{
		this(null);
	}

	ServiceImplementation(final Exception error)
	{
		this.error=error;
	}

	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
	{
		if(error==null)
		{
			enqueuers.intermediateResultEnqueuer(JobType.Protocol).enqueue(JobType.Protocol,method,payload);
		}
		else
		{
			throw error;
		}
	}
}
