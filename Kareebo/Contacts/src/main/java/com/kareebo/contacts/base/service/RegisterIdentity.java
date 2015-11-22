package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.ServiceMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class RegisterIdentity
{
	public static final String serviceName=RegisterIdentity.class.getSimpleName();
	public final static ServiceMethod method0=new ServiceMethod(serviceName,"registerIdentity0");
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"registerIdentity1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"registerIdentity2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"registerIdentity3");
}
