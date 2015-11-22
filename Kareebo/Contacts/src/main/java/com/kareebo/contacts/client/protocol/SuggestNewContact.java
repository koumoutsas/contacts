package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuer;
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
class SuggestNewContact extends Service<com.kareebo.contacts.thrift.SuggestNewContact.VertxClient>
{
	SuggestNewContact(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.SuggestNewContact.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.SuggestNewContact.VertxClient(asyncClientManager);
	}

	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.SuggestNewContact.method0))
		{
			suggestNewContact1((LongId)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.SuggestNewContact.method1))
		{
			suggestNewContact2((EncryptedBuffersWithHashBuffer)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.SuggestNewContact.method2))
		{
			suggestNewContact3((LongId)payload,enqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void suggestNewContact1(final LongId id,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException,
		                                                                                NoSuchProviderException, SignatureException
	{
		asyncClient.suggestNewContact1(id,sign(id),new IntermediateResultHandler<EncryptionKeysWithHashBuffer>(enqueuer,com.kareebo.contacts.base.service.SuggestNewContact.method1));
	}

	private void suggestNewContact2(final EncryptedBuffersWithHashBuffer encryptedBuffersWithHashBuffer,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBuffer> encryptedBuffers=encryptedBuffersWithHashBuffer.getBuffers();
		final HashBuffer uB=encryptedBuffersWithHashBuffer.getUB();
		final Set<EncryptedBufferSigned> encryptedBufferSignedSet=new HashSet<>(encryptedBuffers.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBuffers)
		{
			encryptedBufferSignedSet.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		asyncClient.suggestNewContact2(encryptedBufferSignedSet,uB,sign(uB),new IntermediateVoidResultHandler(enqueuer,com.kareebo.contacts.base.service.SuggestNewContact.method2));
	}

	private void suggestNewContact3(final LongId id,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.suggestNewContact3(id,sign(id),new IntermediateResultHandler<EncryptedBufferSignedWithVerificationKey>(enqueuer,com.kareebo.contacts.base.service.SuggestNewContact.method3));
	}
}
