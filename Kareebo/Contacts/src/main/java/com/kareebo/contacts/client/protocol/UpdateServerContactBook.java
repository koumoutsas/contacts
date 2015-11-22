package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.ContactOperationSet;
import com.kareebo.contacts.thrift.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the update server contact book service.
 */
class UpdateServerContactBook extends Service<com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient>
{
	UpdateServerContactBook(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient(asyncClientManager);
	}

	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.UpdateServerContactBook.method0))
		{
			updateServerContactBook1((ContactOperationSet)payload,enqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void updateServerContactBook1(final ContactOperationSet contactOperationSet,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.updateServerContactBook1(contactOperationSet,sign(contactOperationSet),new FinalResultHandler(enqueuer,com.kareebo.contacts.base.service.UpdateServerContactBook.method1));
	}
}
