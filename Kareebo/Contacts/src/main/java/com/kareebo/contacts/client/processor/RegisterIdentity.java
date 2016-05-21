package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the register identity service
 */
public class RegisterIdentity extends com.kareebo.contacts.client.jobs.Service
{
	public static final String serviceName=RegisterIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	private static final ServiceMethod[] methods={method1,method2};

	protected RegisterIdentity()
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
		return new Functor[]{
			(final TBase payload,final Enqueuers enqueuers)->
			{
			},(final TBase payload,final Enqueuers enqueuers)->{
		}
		};
	}
}
