package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.PublicKeys;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the modify keys service
 */
public class ModifyKeys extends Signer
{
	final private com.kareebo.contacts.thrift.ModifyKeys.VertxClient vertxClient;

	public ModifyKeys(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.ModifyKeys.VertxClient(asyncClientManager);
	}

	public void modifyKeys1(final PublicKeys newPublicKeys,final AsyncResultHandler<Void> handler) throws TException, InvalidKeyException,
		                                                                                                      NoSuchAlgorithmException,
		                                                                                                      NoSuchProviderException, SignatureException
	{
		vertxClient.modifyKeys1(newPublicKeys,sign(newPublicKeys),handler);
	}
}
