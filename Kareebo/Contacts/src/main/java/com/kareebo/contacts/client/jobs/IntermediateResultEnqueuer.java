package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

/// Interface for enqueuing jobs that are not the result of a service or an error
public interface IntermediateResultEnqueuer
{
	/**
	 * Enqueue a job
	 *
	 * @param type    The job type
	 * @param method  The service method that caused the error
	 * @param payload The payload of the operation
	 */
	void enqueue(JobType type,ServiceMethod method,TBase payload);
}
