package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.RegisterIdentityReply;
import com.kareebo.contacts.thrift.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the register identity service
 */
class RegisterIdentity extends com.kareebo.contacts.client.jobs.Service
{
	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.RegisterIdentity.method1))
		{
			registerIdentity1((RegisterIdentityReply)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.RegisterIdentity.method2))
		{
			registerIdentity2((RegisterIdentityReply)payload,enqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}	
	}

	private void registerIdentity1(final RegisterIdentityReply registerIdentityReply,final Enqueuer enqueuer)
	{
	}

	private void registerIdentity2(final RegisterIdentityReply registerIdentityReply,final Enqueuer enqueuer)
	{
	}
}
