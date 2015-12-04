package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.RegisterIdentityReply;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the register identity service
 */
class RegisterIdentity extends com.kareebo.contacts.client.jobs.Service
{
	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.RegisterIdentity.method1))
		{
			registerIdentity1((RegisterIdentityReply)payload,enqueuers);
		}
		else if(method.equals(com.kareebo.contacts.base.service.RegisterIdentity.method2))
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
