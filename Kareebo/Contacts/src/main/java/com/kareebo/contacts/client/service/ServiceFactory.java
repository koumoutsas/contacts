package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.NotificationMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Service factory returning a {@link Service} corresponding to a name
 */
public class ServiceFactory
{
	final private static String packageName=ServiceFactory.class.getPackage().getName()+".";

	/**
	 * Run a method based on its service and method name and a notification id. Service is resolved based on reflection
	 *
	 * @param clientManager  The asynchronous client manager
	 * @param signingKey     The signing key of the client
	 * @param clientId       The client id
	 * @param method         The method to be called
	 * @param notificationId The notification id
	 * @param handler        @throws NoSuchMethod When the method cannot be found
	 * @throws NoSuchService When the service cannot be found
	 */
	public static void run(final TAsyncClientManager clientManager,final SigningKey signingKey,final ClientId clientId,final NotificationMethod method,final long notificationId,final AsyncResultHandler<TBase> handler) throws NoSuchMethod, NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchService
	{
		try
		{
			final Class<?> serviceClass=Class.forName(packageName+method.getServiceName());
			final Constructor<?> constructor=serviceClass.getConstructor(TAsyncClientManager.class,SigningKey.class,ClientId.class);
			final Service service=(Service)constructor.newInstance(clientManager,signingKey,clientId);
			service.run(method,notificationId,handler);
		}
		catch(ClassNotFoundException|NoSuchMethodException|IllegalAccessException|InstantiationException|InvocationTargetException e)
		{
			throw new NoSuchService();
		}
		catch(Service.NoSuchMethod e)
		{
			throw new NoSuchMethod();
		}
	}

	/// Exception thrown when a service cannot be found
	public static class NoSuchService extends Exception
	{
	}

	/// Exception thrown when a method cannot be found
	public static class NoSuchMethod extends Exception
	{
	}
}
