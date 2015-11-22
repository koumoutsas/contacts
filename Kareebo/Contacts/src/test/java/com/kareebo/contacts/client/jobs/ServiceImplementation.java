package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.ServiceMethod;
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
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(error==null)
		{
			enqueuer.processor(method,payload);
		}
		else
		{
			throw error;
		}
	}
}
