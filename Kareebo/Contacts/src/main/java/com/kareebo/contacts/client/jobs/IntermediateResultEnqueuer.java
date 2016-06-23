package com.kareebo.contacts.client.jobs;

import javax.annotation.Nonnull;

/**
 * Interface for enqueuing jobs that are not the result of a service or an error
 */
@FunctionalInterface
public interface IntermediateResultEnqueuer
{
	/**
	 * Enqueue a job
	 *
	 * @param job The job
	 */
	void put(final @Nonnull IntermediateJob job);
}
