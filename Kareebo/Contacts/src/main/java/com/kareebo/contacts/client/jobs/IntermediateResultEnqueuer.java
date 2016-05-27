package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

import javax.annotation.Nonnull;

/// Interface for enqueuing jobs that are not the result of a service or an error
public interface IntermediateResultEnqueuer
{
	/**
	 * Enqueue a job
	 *
	 * @param type    The job type
	 * @param method  The service method that caused the error
	 * @param context The service context
	 * @param payload The payload of the operation
	 */
	void enqueue(@Nonnull JobType type,@Nonnull ServiceMethod method,@Nonnull Context context,@Nonnull TBase payload);
}
