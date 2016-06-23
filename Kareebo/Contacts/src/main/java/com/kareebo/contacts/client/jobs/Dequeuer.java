package com.kareebo.contacts.client.jobs;

/**
 * Interface for dequeuing jobs. Used to construct {@link Runner}. Should be implemented by the platform implementation of a job queue
 */
@FunctionalInterface
public interface Dequeuer
{
	/**
	 * Dequeue a job
	 *
	 * @return A job, null if none is available
	 */
	Job get();
}
