package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.Notification;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;

import javax.annotation.Nonnull;

/**
 * Bridge between notification system and {@link ServiceDispatcher}. Handles incoming notifications and routes them to the correct service method
 */
class NotificationHandler
{
	final private ServiceDispatcher serviceDispatcher;

	NotificationHandler(final @Nonnull ServiceDispatcher serviceDispatcher)
	{
		this.serviceDispatcher=serviceDispatcher;
	}

	/**
	 * Handle a notification and dispatch to the correct service
	 *
	 * @param payload The notification payload
	 * @throws TException When deserialization of the payload fails
	 */
	public void handle(final @Nonnull byte[] payload) throws TException, ServiceDispatcher.NoSuchService, Service.NoSuchMethod, Service.ExecutionFailed
	{
		final Notification notification=new Notification();
		new TDeserializer().deserialize(notification,payload);
		final ServiceMethod notificationMethod=notification.getMethod();
		serviceDispatcher.run(notificationMethod,notification.getId());
	}
}
