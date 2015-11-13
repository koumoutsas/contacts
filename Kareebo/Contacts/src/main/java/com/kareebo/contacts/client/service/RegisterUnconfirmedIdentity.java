package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.HashBufferSet;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the register identity service
 */
public class RegisterUnconfirmedIdentity extends Signer
{
	final private com.kareebo.contacts.thrift.RegisterUnconfirmedIdentity.VertxClient vertxClient;

	RegisterUnconfirmedIdentity(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.RegisterUnconfirmedIdentity.VertxClient(asyncClientManager);
	}

	public void registerUnconfirmedIdentity1(final HashBufferSet uSet,final AsyncResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.registerUnconfirmedIdentity1(uSet,sign(uSet),handler);
	}
}
