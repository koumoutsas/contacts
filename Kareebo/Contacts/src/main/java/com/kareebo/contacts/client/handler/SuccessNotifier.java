package com.kareebo.contacts.client.handler;

/**
 * Interface for notifying when a service has successfully completed
 */
public interface SuccessNotifier
{
	/**
	 * Notify that a service failed
	 *
	 * @param service The service name
	 */
	void notify(String service);
}
