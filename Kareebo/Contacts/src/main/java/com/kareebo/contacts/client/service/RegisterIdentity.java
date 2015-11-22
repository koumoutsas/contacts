package com.kareebo.contacts.client.service;

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
public class RegisterIdentity extends Service<com.kareebo.contacts.thrift.RegisterIdentity.VertxClient>
{
	public RegisterIdentity(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.RegisterIdentity.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.RegisterIdentity.VertxClient(asyncClientManager);
	}

	public void registerIdentity1(final HashBuffer uA,final ResultHandler<RegisterIdentityReply> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.registerIdentity1(uA,sign(uA),new AsyncResultHandler<>(handler));
	}

	public void registerIdentity2(final long userIdA,final ResultHandler<RegisterIdentityReply> handler)
	{
		asyncClient.registerIdentity2(userIdA,new AsyncResultHandler<>(handler));
	}

	public void registerIdentity3(final RegisterIdentityInput registerIdentityInput,final ResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.registerIdentity3(registerIdentityInput,sign(registerIdentityInput),new AsyncResultHandler<>(handler));
	}
}
