package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Base class for all services
 */
abstract public class Service<T extends org.apache.thrift.async.TAsyncClient>
{
	/// The Vertx client
	final protected T asyncClient;
	/// A {@link Signer} for signing payloads
	final private Signer signer;

	Service(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		signer=new Signer(signingKey,clientId);
		asyncClient=construct(asyncClientManager);
	}

	/**
	 * Factory method for creating the Vertx async client
	 *
	 * @param asyncClientManager The async client manager
	 * @return The Vertx async client
	 */
	abstract protected T construct(TAsyncClientManager asyncClientManager);

	protected SignatureBuffer sign(final TBase object) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		return signer.sign(object);
	}
}
