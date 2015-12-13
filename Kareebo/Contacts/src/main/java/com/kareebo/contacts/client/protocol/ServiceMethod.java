package com.kareebo.contacts.client.protocol;

/**
 * Trivial extension for type safety reasons
 */
class ServiceMethod extends com.kareebo.contacts.thrift.client.jobs.ServiceMethod
{
	ServiceMethod(final String serviceName,final String methodName)
	{
		super(serviceName,methodName);
	}
}
