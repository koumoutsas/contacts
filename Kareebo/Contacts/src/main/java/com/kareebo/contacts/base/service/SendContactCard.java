package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class SendContactCard
{
	private static final String serviceName=SendContactCard.class.getSimpleName();
	public final static ServiceMethod method0=new ServiceMethod(serviceName,"sendContactCard0");
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"sendContactCard1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"sendContactCard2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"sendContactCard3");
	public final static ServiceMethod method4=new ServiceMethod(serviceName,"sendContactCard4");
}
