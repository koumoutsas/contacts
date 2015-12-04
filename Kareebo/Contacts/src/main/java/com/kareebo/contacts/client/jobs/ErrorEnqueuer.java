package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;

/// Interface for error job enqueuing
public interface ErrorEnqueuer
{
	/**
	 * Store an error from the protocol side
	 *
	 * @param type   The job type
	 * @param method The service method that caused the error
	 * @param error  The error
	 */
	void error(JobType type,ServiceMethod method,ErrorCode error);
}
