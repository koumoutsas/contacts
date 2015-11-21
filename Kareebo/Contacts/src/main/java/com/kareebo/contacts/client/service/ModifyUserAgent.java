package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.ResultHandler;
import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the modify user agent service
 */
public class ModifyUserAgent extends Signer
{
	final private com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient vertxClient;

	public ModifyUserAgent(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient(asyncClientManager);
	}

	public void modifyUserAgent1(final UserAgent userAgent,final ResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.modifyUserAgent1(userAgent,sign(userAgent),new AsyncResultHandler<>(handler));
	}
}
