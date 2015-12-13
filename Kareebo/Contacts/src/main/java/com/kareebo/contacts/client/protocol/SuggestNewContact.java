package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
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
public class SuggestNewContact extends Service<com.kareebo.contacts.thrift.SuggestNewContact.VertxClient>
{
	public static final String serviceName=SuggestNewContact.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");

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
	protected void runInternal(final com.kareebo.contacts.thrift.client.jobs.ServiceMethod method,final TBase payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final
	FinalResultEnqueuer finalResultEnqueuer) throws Exception
	{
		if(method.equals(method1))
		{
			suggestNewContact1((LongId)payload,intermediateResultEnqueuer,finalResultEnqueuer);
		}
		else if(method.equals(method2))
		{
			suggestNewContact2((EncryptedBuffersWithHashBuffer)payload,finalResultEnqueuer);
		}
		else if(method.equals(method3))
		{
			suggestNewContact3((LongId)payload,intermediateResultEnqueuer,finalResultEnqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void suggestNewContact1(final LongId id,final IntermediateResultEnqueuer intermediateResultEnqueuer,final
	FinalResultEnqueuer finalResultEnqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException,
		                                                NoSuchProviderException, SignatureException
	{
		asyncClient.suggestNewContact1(id,sign(id),new IntermediateResultHandler<EncryptionKeysWithHashBuffer>(intermediateResultEnqueuer,
			                                                                                                      com.kareebo
				                                                                                                      .contacts.client.processor.SuggestNewContact.method1,
				                                                                                                      finalResultEnqueuer,method1));
	}

	private void suggestNewContact2(final EncryptedBuffersWithHashBuffer encryptedBuffersWithHashBuffer,final FinalResultEnqueuer enqueuer) throws
		InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBuffer> encryptedBuffers=encryptedBuffersWithHashBuffer.getBuffers();
		final HashBuffer uB=encryptedBuffersWithHashBuffer.getUB();
		final Set<EncryptedBufferSigned> encryptedBufferSignedSet=new HashSet<>(encryptedBuffers.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBuffers)
		{
			encryptedBufferSignedSet.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		asyncClient.suggestNewContact2(encryptedBufferSignedSet,uB,sign(uB),new IntermediateVoidResultHandler(enqueuer,method2));
	}

	private void suggestNewContact3(final LongId id,final IntermediateResultEnqueuer intermediateResultEnqueuer,final
	FinalResultEnqueuer finalResultEnqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.suggestNewContact3(id,sign(id),new IntermediateResultHandler<EncryptedBufferSignedWithVerificationKey>
			                                           (intermediateResultEnqueuer,com.kareebo.contacts.client.processor
				                                                                       .SuggestNewContact.method3,finalResultEnqueuer,method3));
	}
}
