package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;

import java.util.HashMap;
import java.util.Map;

/// Container for one {@link FinalResultEnqueuer} and a set of {@link IntermediateResultEnqueuer} indexed by {@link JobType}
public class Enqueuers
{
	final private Map<JobType,IntermediateResultEnqueuer> intermediateResultEnqueuers;
	final private FinalResultEnqueuer finalResultEnqueuer;

	/**
	 * Create an {@link Enqueuers} object from a map of {@link IntermediateResultEnqueuer} and a {@link FinalResultEnqueuer}
	 *
	 * @param intermediateResultEnqueuers The map from {@link JobType} to {@link IntermediateResultEnqueuer}
	 * @param finalResultEnqueuer         The {@link FinalResultEnqueuer}
	 */
	public Enqueuers(final Map<JobType,IntermediateResultEnqueuer> intermediateResultEnqueuers,final FinalResultEnqueuer finalResultEnqueuer)
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
	public Enqueuers(final JobType jobType,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer)
	{
		this.intermediateResultEnqueuers=new HashMap<>(1);
		this.intermediateResultEnqueuers.put(jobType,intermediateResultEnqueuer);
		this.finalResultEnqueuer=finalResultEnqueuer;
	}

	/**
	 * Get the {@link FinalResultEnqueuer}
	 *
	 * @return The {@link FinalResultEnqueuer}
	 */
	public FinalResultEnqueuer finalResultEnqueuer()
	{
		return finalResultEnqueuer;
	}

	/**
	 * Get the {@link IntermediateResultEnqueuer} for a {@link JobType}
	 *
	 * @param jobType The {@link JobType}
	 * @return The {@link IntermediateResultEnqueuer}, null if there is no mapping
	 */
	public IntermediateResultEnqueuer intermediateResultEnqueuer(final JobType jobType)
	{
		return intermediateResultEnqueuers.get(jobType);
	}
}
