package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.UserAgent;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the modify user agent service
 */
class ModifyUserAgent extends Service<com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient>
{
	ModifyUserAgent(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient(asyncClientManager);
	}

	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,
	                           final FinalResultEnqueuer finalResultEnqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.ModifyUserAgent.method0))
		{
			modifyUserAgent1((UserAgent)payload,finalResultEnqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void modifyUserAgent1(final UserAgent userAgent,final FinalResultEnqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.modifyUserAgent1(userAgent,sign(userAgent),new FinalResultHandler(enqueuer,com.kareebo.contacts.base.service
			                                                                                       .ModifyUserAgent.method1));
	}
}
