package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.ResultHandler;
import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.RegisterIdentityInput;
import com.kareebo.contacts.thrift.RegisterIdentityReply;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the register identity service
 */
public class RegisterIdentity extends Signer
{
	final private com.kareebo.contacts.thrift.RegisterIdentity.VertxClient vertxClient;

	public RegisterIdentity(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.RegisterIdentity.VertxClient(asyncClientManager);
	}

	public void registerIdentity1(final HashBuffer uA,final ResultHandler<RegisterIdentityReply> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.registerIdentity1(uA,sign(uA),new AsyncResultHandler<>(handler));
	}

	public void registerIdentity2(final long userIdA,final ResultHandler<RegisterIdentityReply> handler)
	{
		vertxClient.registerIdentity2(userIdA,new AsyncResultHandler<>(handler));
	}

	public void registerIdentity3(final RegisterIdentityInput registerIdentityInput,final ResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.registerIdentity3(registerIdentityInput,sign(registerIdentityInput),new AsyncResultHandler<>(handler));
	}
}
