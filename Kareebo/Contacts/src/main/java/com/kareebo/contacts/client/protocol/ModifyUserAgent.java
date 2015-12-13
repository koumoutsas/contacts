package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.UserAgent;
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
public class ModifyUserAgent extends Service<com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient>
{
	public static final String serviceName=ModifyUserAgent.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");

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
	protected void runInternal(final com.kareebo.contacts.thrift.client.jobs.ServiceMethod method,final TBase payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,
	                           final FinalResultEnqueuer finalResultEnqueuer) throws Exception
	{
		if(method.equals(method1))
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
		asyncClient.modifyUserAgent1(userAgent,sign(userAgent),new FinalResultHandler(enqueuer,method1));
	}
}
