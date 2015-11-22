package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.ServiceMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class BroadcastNewContactIdentity
{
	public static final String serviceName=BroadcastNewContactIdentity.class.getSimpleName();
	public final static ServiceMethod method0=new ServiceMethod(serviceName,"broadcastNewContactIdentity0");
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"broadcastNewContactIdentity1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"broadcastNewContactIdentity2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"broadcastNewContactIdentity3");
	public final static ServiceMethod method4=new ServiceMethod(serviceName,"broadcastNewContactIdentity4");
	public final static ServiceMethod method5=new ServiceMethod(serviceName,"broadcastNewContactIdentity5");
}
