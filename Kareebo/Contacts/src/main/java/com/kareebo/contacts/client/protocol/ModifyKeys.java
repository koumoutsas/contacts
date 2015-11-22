package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.PublicKeys;
import com.kareebo.contacts.thrift.ServiceMethod;
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
class ModifyKeys extends Service<com.kareebo.contacts.thrift.ModifyKeys.VertxClient>
{
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
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.ModifyKeys.method0))
		{
			modifyKeys1((PublicKeys)payload,enqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void modifyKeys1(final PublicKeys newPublicKeys,final Enqueuer enqueuer) throws TException, InvalidKeyException,
		                                                                                        NoSuchAlgorithmException,
		                                                                                        NoSuchProviderException, SignatureException
	{
		asyncClient.modifyKeys1(newPublicKeys,sign(newPublicKeys),new FinalResultHandler(enqueuer,com.kareebo.contacts.base.service
			                                                                                          .ModifyKeys.method1));
	}
}
