package com.kareebo.contacts.client.processor;

import javax.annotation.Nonnull;

/**
 * Trivial extension for type safety reasons
 */
public class ServiceMethod extends com.kareebo.contacts.thrift.client.jobs.ServiceMethod
{
	public ServiceMethod(final @Nonnull String serviceName,final String methodName)
	{
		super(serviceName,methodName);
	}
}
