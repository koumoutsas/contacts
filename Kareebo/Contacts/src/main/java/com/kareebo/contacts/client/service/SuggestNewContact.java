package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.HashSet;
import java.util.Set;

/**
 * Client-side implementation of the suggest new contact service
 */
public class SuggestNewContact extends Signer
{
	final private com.kareebo.contacts.thrift.SuggestNewContact.VertxClient vertxClient;

	public SuggestNewContact(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.SuggestNewContact.VertxClient(asyncClientManager);
	}

	public void suggestNewContact1(final LongId id,final AsyncResultHandler<EncryptionKeysWithHashBuffer> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.suggestNewContact1(id,sign(id),handler);
	}

	public void suggestNewContact2(final Set<EncryptedBuffer> encryptedBuffers,final HashBuffer uB,final AsyncResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBufferSigned> encryptedBufferSignedSet=new HashSet<>(encryptedBuffers.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBuffers)
		{
			encryptedBufferSignedSet.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		vertxClient.suggestNewContact2(encryptedBufferSignedSet,uB,sign(uB),handler);
	}

	public void suggestNewContact3(final LongId id,final AsyncResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.suggestNewContact3(id,sign(id),handler);
	}
}
