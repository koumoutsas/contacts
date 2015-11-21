package com.kareebo.contacts.client.handler;

/**
 * Interface for notifying for errors in the service
 */
public interface ErrorNotifier
{
	/**
	 * Notify that a service failed
	 *
	 * @param service The service name
	 * @param cause   The cause of the error
	 */
	void notify(String service,Throwable cause);
}
