package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class SuggestNewContact
{
	public static final String serviceName=SuggestNewContact.class.getSimpleName();
	public final static ServiceMethod method0=new ServiceMethod(serviceName,"suggestNewContact0");
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"suggestNewContact1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"suggestNewContact2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"suggestNewContact3");
}
