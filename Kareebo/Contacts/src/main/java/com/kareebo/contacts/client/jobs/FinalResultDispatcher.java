package com.kareebo.contacts.client.jobs;

import javax.annotation.Nonnull;

/**
 * Dispatcher for {@link ErrorJob}s and {@link SuccessJob}s. Used to construct {@link Dispatcher}
 */
interface FinalResultDispatcher
{
	void dispatch(@Nonnull final ErrorJob errorJob);

	void dispatch(@Nonnull final SuccessJob successJob);
}
