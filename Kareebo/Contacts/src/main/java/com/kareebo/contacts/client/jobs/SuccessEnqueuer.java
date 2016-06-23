package com.kareebo.contacts.client.jobs;

import javax.annotation.Nonnull;

/**
 * Interface for success job enqueuing. It's purpose is to put a success event in the job queue
 */
@FunctionalInterface
interface SuccessEnqueuer
{
	/**
	 * Store a success for a service
	 *
	 * @param job The job
	 */
	void success(@Nonnull SuccessJob job);
}
