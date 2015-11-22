package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Receive and store persistently a job
 */
public interface Enqueuer
{
	/**
	 * Store an error from the processor side
	 *
	 * @param method The service method that caused the error
	 * @param cause  The cause of the error
	 */
	void processorError(ServiceMethod method,Throwable cause);

	/**
	 * Store an error from the protocol side
	 *
	 * @param method The service method that caused the error
	 * @param cause  The cause of the error
	 */
	void protocolError(ServiceMethod method,Throwable cause);

	/**
	 * Store a success for a service
	 *
	 * @param service The service name
	 */
	void success(String service);

	/**
	 * Store a job to the processor side
	 *
	 * @param method  The service method that spawned the job
	 * @param payload The payload of the job
	 */
	void processor(ServiceMethod method,TBase payload);

	/**
	 * Store a job to the protocol side
	 *
	 * @param method  The service method that spawned the job
	 * @param payload The payload of the job
	 */
	void protocol(ServiceMethod method,TBase payload);
}
