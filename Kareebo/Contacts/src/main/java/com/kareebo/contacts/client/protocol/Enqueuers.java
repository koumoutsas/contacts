package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.JobType;

import javax.annotation.Nonnull;

/**
 * Extension of {@link com.kareebo.contacts.client.jobs.Enqueuers} that ensures the proper enqueuer types are used
 */
public class Enqueuers extends com.kareebo.contacts.client.jobs.Enqueuers
{
	/**
	 * Construct an {@link Enqueuers} object from a processor {@link IntermediateResultEnqueuer} and a {@link FinalResultEnqueuer}
	 *
	 * @param intermediateResultEnqueuer The {@link IntermediateResultEnqueuer}
	 * @param finalResultEnqueuer        The {@link FinalResultEnqueuer}
	 */
	public Enqueuers(@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer)
	{
		super(JobType.Processor,intermediateResultEnqueuer,finalResultEnqueuer);
	}
}
