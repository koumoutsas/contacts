package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.ServiceMethod;
import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for all services
 */
abstract public class Service
{
	private static final Logger logger=LoggerFactory.getLogger(Service.class.getName());

	/**
	 * Run a method based on its name and a payload
	 *
	 * @param method   The method to be called
	 * @param payload  The method payload
	 * @param enqueuer The resulting job enqueuer
	 * @throws NoSuchMethod    When the method cannot be found
	 * @throws ExecutionFailed When the method was invoked, but its execution failed
	 */
	void run(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws NoSuchMethod, ExecutionFailed
	{
		try
		{
			runInternal(method,payload,enqueuer);
		}
		catch(NoSuchMethod e)
		{
			logger.error("Unknown method "+method.getServiceName()+"."+method.getMethodName(),e);
			throw e;
		}
		catch(ClassCastException e)
		{
			logger.error("Invalid argument type "+(payload!=null?payload.getClass().getSimpleName():"null")+" for trigger method "+method
				                                                                                                                       .getServiceName()+""+
				             "."+method
					                 .getMethodName(),e);
			throw new NoSuchMethod();
		}
		catch(Exception e)
		{
			logger.error("Execution failed for method "+method.getServiceName()+"."+method.getMethodName(),e);
			throw new ExecutionFailed();
		}
	}

	abstract protected void runInternal(ServiceMethod method,TBase payload,Enqueuer enqueuer) throws Exception;

	/// Exception thrown when a method cannot be found
	public static class NoSuchMethod extends Exception
	{
	}

	/// Exception thrown when executing a method fails
	public static class ExecutionFailed extends Exception
	{
	}
}
