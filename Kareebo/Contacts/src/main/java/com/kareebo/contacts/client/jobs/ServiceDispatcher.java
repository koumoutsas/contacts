package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Reflection-based service dispatcher
 */
abstract public class ServiceDispatcher
{
	private static final Logger logger=LoggerFactory.getLogger(ServiceDispatcher.class.getName());
	final private String packageName=getClass().getPackage().getName()+".";
	final private Enqueuers enqueuers;

	public ServiceDispatcher(final Enqueuers enqueuers)
	{
		this.enqueuers=enqueuers;
	}

	/**
	 * Run a method based on its service and method name and a notification id. Service is resolved based on reflection
	 *
	 * @param method         The method to be called
	 * @param notificationId The notification id
	 * @throws NoSuchService When the service cannot be found
	 */
	public void run(final ServiceMethod method,final long notificationId) throws Service.ExecutionFailed, NoSuchService, Service.NoSuchMethod
	{
		run(method,new LongId(notificationId),null);
	}

	/**
	 * Run a method based on its service and method name and a payload. Service is resolved based on reflection
	 *
	 * @param method  The method to be called
	 * @param payload The payload
	 * @param context The service context
	 * @throws NoSuchService        When the service cannot be found
	 * @throws Service.NoSuchMethod When the service method cannot be found
	 */
	public void run(final ServiceMethod method,final TBase payload,final Context context) throws Service.NoSuchMethod, Service.ExecutionFailed, NoSuchService
	{
		try
		{
			constructService(Class.forName(packageName+method.getServiceName()),context).run(method,payload,enqueuers);
		}
		catch(ClassNotFoundException|NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e)
		{
			logger.error("Invalid service "+packageName+(method.getServiceName()),e);
			throw new NoSuchService();
		}
	}

	/**
	 * Construct a {@link Service} from class name and context
	 *
	 * @param theClass The class name
	 * @param context  The context
	 * @return The {@link Service}
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	abstract public Service constructService(Class<?> theClass,final Context context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/// Exception thrown when a service cannot be found
	public static class NoSuchService extends Exception
	{
	}
}
