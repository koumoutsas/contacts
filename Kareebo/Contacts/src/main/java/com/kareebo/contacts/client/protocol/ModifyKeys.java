package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.PublicKeys;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the modify keys service
 */
public class ModifyKeys extends Service<com.kareebo.contacts.thrift.ModifyKeys.VertxClient>
{
	public static final String serviceName=ModifyKeys.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");

	ModifyKeys(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.ModifyKeys.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.ModifyKeys.VertxClient(asyncClientManager);
	}

	@Override
	protected void runInternal(final com.kareebo.contacts.thrift.client.jobs.ServiceMethod method,final TBase payload,final
	IntermediateResultEnqueuer intermediateResultEnqueuer,
	                           final FinalResultEnqueuer finalResultEnqueuer) throws Exception
	{
		if(method.equals(method1))
		{
			modifyKeys1((PublicKeys)payload,finalResultEnqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void modifyKeys1(final PublicKeys newPublicKeys,final FinalResultEnqueuer enqueuer) throws TException, InvalidKeyException,
		                                                                                                   NoSuchAlgorithmException,
		                                                                                                   NoSuchProviderException, SignatureException
	{
		asyncClient.modifyKeys1(newPublicKeys,sign(newPublicKeys),new FinalResultHandler(enqueuer,method1));
	}
}
