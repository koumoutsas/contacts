package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the register identity service
 */
class RegisterIdentity extends Service<com.kareebo.contacts.thrift.RegisterIdentity.VertxClient>
{
	RegisterIdentity(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.RegisterIdentity.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.RegisterIdentity.VertxClient(asyncClientManager);
	}

	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.RegisterIdentity.method0))
		{
			registerIdentity1((HashBuffer)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.RegisterIdentity.method1))
		{
			registerIdentity2(((LongId)payload).getId(),enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.RegisterIdentity.method2))
		{
			registerIdentity3((RegisterIdentityInput)payload,enqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void registerIdentity1(final HashBuffer uA,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException,
		                                                                                   NoSuchProviderException, SignatureException
	{
		asyncClient.registerIdentity1(uA,sign(uA),new IntermediateResultHandler<RegisterIdentityReply>(enqueuer,com.kareebo.contacts.base
			                                                                                                        .service.RegisterIdentity.method1));
	}

	private void registerIdentity2(final long userIdA,final Enqueuer enqueuer)
	{
		asyncClient.registerIdentity2(userIdA,new IntermediateResultHandler<RegisterIdentityReply>(enqueuer,com.kareebo.contacts.base
			                                                                                                    .service
			                                                                                                    .RegisterIdentity.method2));
	}

	private void registerIdentity3(final RegisterIdentityInput registerIdentityInput,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.registerIdentity3(registerIdentityInput,sign(registerIdentityInput),new FinalResultHandler(enqueuer,com.kareebo.contacts.base
			                                                                                                                .service.RegisterIdentity.method3));
	}
}
