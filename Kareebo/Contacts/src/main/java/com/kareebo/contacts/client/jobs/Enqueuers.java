package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

/**
 * Container for one {@link FinalResultEnqueuer} and a set of {@link IntermediateResultEnqueuer} indexed by {@link JobType}. Used for constructing
 * {@link ServiceDispatcher}
 */
public class Enqueuers
{
	final private Map<JobType,IntermediateResultEnqueuer> intermediateResultEnqueuers;
	final private FinalResultEnqueuer finalResultEnqueuer;

	/**
	 * Create an {@link Enqueuers} object from a map of {@link IntermediateResultEnqueuer} and a {@link FinalResultEnqueuer}
	 *
	 * @param intermediateResultEnqueuers The map from {@link JobType} to {@link IntermediateResultEnqueuer}. We expect each {@link JobType} to
	 *                                           have different queueing behavior, which is why there are different
	 *                                           {@link IntermediateResultEnqueuer}s for each {@link JobType}
	 * @param finalResultEnqueuer         The {@link FinalResultEnqueuer}. The reason there is only one {@link ErrorEnqueuer} and
	 * {@link SuccessEnqueuer} is that it is expected that final events are handled in a uniform way, regardless of their type. More
	 *                                           fine-grained control can be implemented in the concrete class
	 */
	public Enqueuers(final @Nonnull Map<JobType,IntermediateResultEnqueuer> intermediateResultEnqueuers,final @Nonnull FinalResultEnqueuer finalResultEnqueuer)
	{
		this.intermediateResultEnqueuers=intermediateResultEnqueuers;
		this.finalResultEnqueuer=finalResultEnqueuer;
	}

	/**
	 * Shortcut constructor with a single {@link IntermediateResultEnqueuer}
	 *
	 * @param jobType                    The {@link JobType}
	 * @param intermediateResultEnqueuer The {@link IntermediateResultEnqueuer}
	 * @param finalResultEnqueuer        The {@link FinalResultEnqueuer}
	 */
	public Enqueuers(final @Nonnull JobType jobType,final @Nonnull IntermediateResultEnqueuer intermediateResultEnqueuer,final @Nonnull FinalResultEnqueuer finalResultEnqueuer)
	{
		this.intermediateResultEnqueuers=Collections.singletonMap(jobType,intermediateResultEnqueuer);
		this.finalResultEnqueuer=finalResultEnqueuer;
	}

	/**
	 * Get the {@link FinalResultEnqueuer}
	 *
	 * @return The {@link FinalResultEnqueuer}
	 */
	public
	@Nonnull
	FinalResultEnqueuer finalResultEnqueuer()
	{
		return finalResultEnqueuer;
	}

	/**
	 * Get the {@link IntermediateResultEnqueuer} for a {@link JobType}
	 *
	 * @param jobType The {@link JobType}
	 * @return The {@link IntermediateResultEnqueuer}, null if there is no mapping
	 */
	public IntermediateResultEnqueuer intermediateResultEnqueuer(final @Nonnull JobType jobType)
	{
		return intermediateResultEnqueuers.get(jobType);
	}
}
