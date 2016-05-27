package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.TBase;

import javax.annotation.Nonnull;

/**
 * Client-processor-side implementation of the register identity service
 */
public class RegisterIdentity extends com.kareebo.contacts.client.jobs.Service
{
	public static final String serviceName=RegisterIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	private static final ServiceMethod[] methods={method1,method2};

	protected RegisterIdentity(final @Nonnull Context context)
	{
		super(context);
	}

	@Nonnull
	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return methods;
	}

	@Nonnull
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
