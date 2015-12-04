package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class RegisterUnconfirmedIdentity
{
	public static final String serviceName=RegisterUnconfirmedIdentity.class.getSimpleName();
	public final static ServiceMethod method0=new ServiceMethod(serviceName,"registerUnconfirmedIdentity0");
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"registerUnconfirmedIdentity1");
}
