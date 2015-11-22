package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.ServiceMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class ModifyKeys
{
	public static final String serviceName=ModifyKeys.class.getSimpleName();
	public final static ServiceMethod method0=new ServiceMethod(serviceName,"modifyKeys0");
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"modifyKeys1");
}
