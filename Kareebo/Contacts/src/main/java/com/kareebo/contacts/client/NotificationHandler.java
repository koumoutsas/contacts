package com.kareebo.contacts.client;

import com.kareebo.contacts.client.service.ResultHandler;
import com.kareebo.contacts.client.service.ServiceFactory;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.Notification;
import com.kareebo.contacts.thrift.NotificationMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Handles incoming notifications and routes them to the correct service method
 */
public class NotificationHandler
{
	final private TAsyncClientManager clientManager;
	final private SigningKey signingKey;
	final private ClientId clientId;

	public NotificationHandler(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		this.clientManager=asyncClientManager;
		this.signingKey=signingKey;
		this.clientId=clientId;
	}

	/**
	 * Handle a notification and dispatch to the correct service
	 *
	 * @param payload The notification payload
	 * @throws TException           When deserialization of the payload fails
	 * @throws InvalidServiceMethod When the service or the method in the notification cannot be found
	 */
	public void handle(final byte[] payload,final ResultHandler<TBase> handler) throws TException, InvalidServiceMethod, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
	{
		final Notification notification=new Notification();
		new TDeserializer().deserialize(notification,payload);
		final NotificationMethod notificationMethod=notification.getMethod();
		try
		{
			ServiceFactory.run(clientManager,signingKey,clientId,notificationMethod,notification.getId(),handler);
		}
		catch(ServiceFactory.NoSuchService noSuchService)
		{
			throw new InvalidServiceMethod(notificationMethod.getServiceName());
		}
		catch(ServiceFactory.NoSuchMethod noSuchMethod)
		{
			throw new InvalidServiceMethod(notificationMethod);
		}
	}

	/**
	 * Exception thrown when a method or service cannot be found
	 */
	public class InvalidServiceMethod extends Exception
	{
		private InvalidServiceMethod(final NotificationMethod method)
		{
			super("Unknown method "+method.getServiceName()+'.'+method.getMethodName());
		}

		private InvalidServiceMethod(final String service)
		{
			super("Unknown service "+service);
		}
	}
}
