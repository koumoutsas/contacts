package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;

import javax.annotation.Nonnull;

/// Interface for success and error job enqueuing
public interface FinalResultEnqueuer extends ErrorEnqueuer
{
	/**
	 * Store a success for a service
	 *
	 * @param type    The job type
	 * @param service The service name
	 * @param result  The result
	 */
	void success(@Nonnull JobType type,@Nonnull String service,SuccessCode result);
}
