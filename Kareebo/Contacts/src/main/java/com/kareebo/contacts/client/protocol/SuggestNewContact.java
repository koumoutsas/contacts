package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.async.TAsyncClientManager;

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
	private final static ServiceMethod[] methods={method1,method2,method3};

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
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return methods;
	}

	@Override
	protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
	{
		return new com.kareebo.contacts.client.jobs.Service.Functor[]{
			new Functor<LongId>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.SuggestNewContact.VertxClient asyncClient,final LongId payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.suggestNewContact1(payload,sign(payload),new IntermediateResultHandler<EncryptionKeysWithHashBuffer>
						                                                     (intermediateResultEnqueuer,
							                                                     com.kareebo
								                                                     .contacts.client.processor.SuggestNewContact.method1,
								                                                     finalResultEnqueuer,method1));
				}
			},
			new Functor<EncryptedBuffersWithHashBuffer>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.SuggestNewContact.VertxClient asyncClient,final EncryptedBuffersWithHashBuffer payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					final Set<EncryptedBuffer> encryptedBuffers=payload.getBuffers();
					final HashBuffer uB=payload.getUB();
					final Set<EncryptedBufferSigned> encryptedBufferSignedSet=new HashSet<>(encryptedBuffers.size());
					for(final EncryptedBuffer encryptedBuffer : encryptedBuffers)
					{
						encryptedBufferSignedSet.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
					}
					asyncClient.suggestNewContact2(encryptedBufferSignedSet,uB,sign(uB),new IntermediateVoidResultHandler(finalResultEnqueuer,method2));
				}
			},
			new Functor<LongId>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.SuggestNewContact.VertxClient asyncClient,final LongId payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.suggestNewContact3(payload,sign(payload),new IntermediateResultHandler<EncryptedBufferSignedWithVerificationKey>
						                                                     (intermediateResultEnqueuer,com.kareebo.contacts.client.processor
							                                                                                 .SuggestNewContact.method3,finalResultEnqueuer,method3));
				}
			}
		};
	}
}
