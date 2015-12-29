package com.kareebo.contacts.client.processor;

/**
 * Client-processor-side implementation of the broadcast new contact identity service
 */
public class BroadcastNewContactIdentity extends com.kareebo.contacts.client.jobs.Service
{
	private static final String serviceName=BroadcastNewContactIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");
	public final static ServiceMethod method4=new ServiceMethod(serviceName,"4");
	private static final ServiceMethod[] methods={method1,method2,method3,method4};

	protected BroadcastNewContactIdentity()
	{
		super(null);
	}

	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return methods;
	}

	@Override
	protected Functor[] functors()
	{
		return new Functor[0];
	}
}
