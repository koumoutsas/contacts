package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.LongId;
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
		run(method,new LongId(notificationId));
	}

	/**
	 * Run a method based on its service and method name and a payload. Service is resolved based on reflection
	 *
	 * @param method  The method to be called
	 * @param payload The payload
	 * @throws NoSuchService        When the service cannot be found
	 * @throws Service.NoSuchMethod When the service method cannot be found
	 */
	public void run(final ServiceMethod method,final TBase payload) throws Service.NoSuchMethod, Service.ExecutionFailed, NoSuchService
	{
		try
		{
			constructService(Class.forName(packageName+method.getServiceName())).run(method,payload,enqueuers);
		}
		catch(ClassNotFoundException|NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e)
		{
			logger.error("Invalid service "+packageName+(method.getServiceName()),e);
			throw new NoSuchService();
		}
	}

	abstract public Service constructService(Class<?> theClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/// Exception thrown when a service cannot be found
	public static class NoSuchService extends Exception
	{
	}
}
