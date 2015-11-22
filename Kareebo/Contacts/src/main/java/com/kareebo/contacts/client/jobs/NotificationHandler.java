package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.Notification;
import com.kareebo.contacts.thrift.ServiceMethod;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;

/**
 * Handles incoming notifications and routes them to the correct service method
 */
public class NotificationHandler
{
	final private ServiceDispatcher serviceDispatcher;

	public NotificationHandler(final ServiceDispatcher serviceDispatcher)
	{
		this.serviceDispatcher=serviceDispatcher;
	}

	/**
	 * Handle a notification and dispatch to the correct service
	 *
	 * @param payload The notification payload
	 * @throws TException When deserialization of the payload fails
	 */
	public void handle(final byte[] payload) throws TException, ServiceDispatcher.NoSuchService, Service.NoSuchMethod, Service.ExecutionFailed
	{
		final Notification notification=new Notification();
		new TDeserializer().deserialize(notification,payload);
		final ServiceMethod notificationMethod=notification.getMethod();
		serviceDispatcher.run(notificationMethod,notification.getId());
	}
}
