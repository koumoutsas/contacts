package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TBase;
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
public class SuggestNewContact extends Signer implements Service
{
	final private com.kareebo.contacts.thrift.SuggestNewContact.VertxClient vertxClient;

	public SuggestNewContact(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.SuggestNewContact.VertxClient(asyncClientManager);
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

	@Override
	public void run(final NotificationMethod method,final long notificationId,final AsyncResultHandler<TBase> handler) throws NoSuchMethod, NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		if(method.equals(com.kareebo.contacts.base.service.SuggestNewContact.method0))
		{
			suggestNewContact1(notificationId,new AsyncResultConnector<EncryptionKeysWithHashBuffer>(handler));
		}
		else if(method.equals(com.kareebo.contacts.base.service.SuggestNewContact.method2))
		{
			suggestNewContact3(notificationId,new AsyncResultConnector<EncryptedBufferSignedWithVerificationKey>(handler));
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void suggestNewContact1(final long id,final AsyncResultHandler<EncryptionKeysWithHashBuffer> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final LongId longId=new LongId(id);
		vertxClient.suggestNewContact1(longId,sign(longId),handler);
	}

	private void suggestNewContact3(final long id,final AsyncResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final LongId longId=new LongId(id);
		vertxClient.suggestNewContact3(longId,sign(longId),handler);
	}
}
