package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.RegisterIdentityReply;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the register identity service
 */
public class RegisterIdentity extends com.kareebo.contacts.client.jobs.Service
{
	public static final String serviceName=RegisterIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");

	@Override
	protected void runInternal(final com.kareebo.contacts.thrift.client.jobs.ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
	{
		if(method.equals(method1))
		{
			registerIdentity1((RegisterIdentityReply)payload,enqueuers);
		}
		else if(method.equals(method2))
		{
			registerIdentity2((RegisterIdentityReply)payload,enqueuers);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void registerIdentity1(final RegisterIdentityReply registerIdentityReply,final Enqueuers enqueuers)
	{
	}

	private void registerIdentity2(final RegisterIdentityReply registerIdentityReply,final Enqueuers enqueuers)
	{
	}
}
