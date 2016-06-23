package com.kareebo.contacts.client.jobs;

/**
 * Convenience extension of {@link ErrorEnqueuer} and {@link SuccessEnqueuer}. Used to construct {@link Enqueuers}
 */
public interface FinalResultEnqueuer extends ErrorEnqueuer, SuccessEnqueuer
{
}
