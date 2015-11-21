package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.ResultHandler;
import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.ContactOperationSet;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the update server contact book service.
 */
public class UpdateServerContactBook extends Signer
{
	final private com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient vertxClient;

	public UpdateServerContactBook(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient(asyncClientManager);
	}

	public void updateServerContactBook1(final ContactOperationSet contactOperationSet,final ResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.updateServerContactBook1(contactOperationSet,sign(contactOperationSet),new AsyncResultHandler<>(handler));
	}
}
