package com.kareebo.contacts.client.jobs;

import javax.annotation.Nonnull;

/**
 * Interface for error job enqueuing. It's purpose is to put an error in the job queue
 */
@FunctionalInterface
public interface ErrorEnqueuer
{
	/**
	 * Store an error from the protocol side
	 *
	 * @param job   The job
	 */
	void error(@Nonnull ErrorJob job);
}
