package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.thrift.FailedOperation;

import javax.annotation.Nonnull;

/**
 * Interface to the push notification system
 */
interface ClientNotifierBackend
{
	/**
	 * Send a notification to a client
	 *
	 * @param deviceToken The device token for the push notification backend
	 * @param payload     The payload of the notification
	 * @throws FailedOperation If the notification could not be sent
	 */
	void notify(final long deviceToken,final @Nonnull byte[] payload) throws FailedOperation;
}
