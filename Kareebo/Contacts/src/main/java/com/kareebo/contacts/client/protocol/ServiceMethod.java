package com.kareebo.contacts.client.protocol;

import javax.annotation.Nonnull;

/**
 * Trivial extension for type safety reasons
 */
public class ServiceMethod extends com.kareebo.contacts.thrift.client.jobs.ServiceMethod
{
	public ServiceMethod(@Nonnull final String serviceName,final String methodName)
	{
		super(serviceName,methodName);
	}
}
