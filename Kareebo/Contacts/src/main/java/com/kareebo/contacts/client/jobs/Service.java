package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for all services
 */
abstract public class Service
{
	private static final Logger logger=LoggerFactory.getLogger(Service.class.getName());
	/// The set of methods indexed by method name
	private final Map<String,Functor> methods;

	protected Service()
	{
		final ServiceMethod[] methodNames=methodNames();
		final int numberOfMethods=methodNames.length;
		final Functor[] functors=functors();
		final int numberOFunctors=functors.length;
		if(numberOfMethods!=numberOFunctors)
		{
			throw new IllegalArgumentException("Unequal number of names ("+numberOfMethods+") and methods ("+numberOFunctors+')');
		}
		methods=new HashMap<>(numberOfMethods);
		for(int i=0;i<numberOfMethods;++i)
		{
			final String name=methodNames[i].getMethodName();
			if(methods.put(name,functors[i])!=null)
			{
				throw new IllegalArgumentException("Method name "+name+" defined twice");
			}
		}
	}

	/// Get the method names
	abstract protected ServiceMethod[] methodNames();

	/// Get the functors for {@link #methodNames}
	abstract protected Functor[] functors();

	/**
	 * Run a method based on its name and a payload
	 *
	 * @param method    The method to be called
	 * @param payload   The method payload
	 * @param enqueuers The available job enqueuers
	 * @throws NoSuchMethod    When the method cannot be found
	 * @throws ExecutionFailed When the method was invoked, but its execution failed
	 */
	void run(final ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws NoSuchMethod, ExecutionFailed
	{
		try
		{
			final Functor f=methods.get(method.getMethodName());
			if(f==null)
			{
				throw new NoSuchMethod();
			}
			f.run(payload,enqueuers);
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

	/// Functor interface for service methods
	protected interface Functor
	{
		void run(TBase payload,Enqueuers enqueuers) throws Exception;
	}

	/// Exception thrown when a method cannot be found
	public static class NoSuchMethod extends Exception
	{
	}

	/// Exception thrown when executing a method fails
	public static class ExecutionFailed extends Exception
	{
	}
}
