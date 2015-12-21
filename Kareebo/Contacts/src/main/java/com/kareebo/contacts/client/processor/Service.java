package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.apache.thrift.TBase;

abstract class Service extends com.kareebo.contacts.client.jobs.Service
{
	final private PersistedObjectRetriever persistedObjectRetriever;

	Service(final PersistedObjectRetriever persistedObjectRetriever)
	{
		this.persistedObjectRetriever=persistedObjectRetriever;
	}

	protected abstract class Functor<S extends TBase> implements com.kareebo.contacts.client.jobs.Service.Functor
	{
		@Override
		public void run(final TBase payload,final Enqueuers enqueuers) throws Exception
		{
			final IntermediateResultEnqueuer intermediateResultEnqueuer=enqueuers.intermediateResultEnqueuer(JobType.Protocol);
			if(intermediateResultEnqueuer==null)
			{
				throw new IllegalArgumentException("No enqueuer for the protocol job type");
			}
			// The cast error exception is caught in com.kareebo.contacts.client.jobs.Service#run
			//noinspection unchecked
			runInternal(persistedObjectRetriever,(S)payload,intermediateResultEnqueuer,enqueuers.finalResultEnqueuer());
		}

		abstract protected void runInternal(PersistedObjectRetriever persistedObjectRetriever,S payload,IntermediateResultEnqueuer intermediateResultEnqueuer,
		                                    FinalResultEnqueuer
			                                    finalResultEnqueuer) throws Exception;
	}
}
