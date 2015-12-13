package com.kareebo.contacts.client.processor;

/**
 * Client-processor-side implementation of the send contact card service
 */
public class SendContactCard extends com.kareebo.contacts.client.jobs.Service
{
	private static final String serviceName=SendContactCard.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");
	public final static ServiceMethod method4=new ServiceMethod(serviceName,"4");

	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return new com.kareebo.contacts.thrift.client.jobs.ServiceMethod[0];
	}

	@Override
	protected Functor[] functors()
	{
		return new Functor[0];
	}
}
