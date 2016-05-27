package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.thrift.client.jobs.Context;

import javax.annotation.Nonnull;

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

	protected SendContactCard(final @Nonnull Context context)
	{
		super(context);
	}

	@Nonnull
	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return new com.kareebo.contacts.thrift.client.jobs.ServiceMethod[0];
	}

	@Nonnull
	@Override
	protected Functor[] functors()
	{
		return new Functor[0];
	}
}
