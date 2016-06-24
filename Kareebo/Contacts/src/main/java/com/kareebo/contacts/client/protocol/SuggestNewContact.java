package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;

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

	SuggestNewContact(@Nonnull final Context context,@Nonnull final TAsyncClientManager asyncClientManager,@Nonnull final SigningKey signingKey,@Nonnull final ClientId clientId)
	{
		super(context,asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.SuggestNewContact.VertxClient construct(@Nonnull final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.SuggestNewContact.VertxClient(asyncClientManager);
	}

	@Nonnull
	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return methods;
	}

	@Nonnull
	@Override
	protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
	{
		return new com.kareebo.contacts.client.jobs.Service.Functor[]{
			new Functor<LongId>()
			{
				@Override
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.SuggestNewContact.VertxClient asyncClient,@Nonnull final LongId payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.suggestNewContact1(payload,sign(payload),new IntermediateResultHandler<>
						                                                     (intermediateResultEnqueuer,
							                                                     com.kareebo
								                                                     .contacts.client.processor.SuggestNewContact.method1,
							                                                     finalResultEnqueuer,method1,context));
				}
			},
			new Functor<EncryptedBuffersWithHashBuffer>()
			{
				@Override
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.SuggestNewContact.VertxClient asyncClient,@Nonnull final EncryptedBuffersWithHashBuffer payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					final HashBuffer uB=payload.getUB();
					asyncClient.suggestNewContact2(sign(payload.getBuffers(),EncryptedBufferSigned::new),uB,sign(uB),new
						                                                                                                 IntermediateVoidResultHandler
						                                                                                                 (finalResultEnqueuer,
							                                                                                                 method2));
				}
			},
			new Functor<LongId>()
			{
				@Override
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.SuggestNewContact.VertxClient asyncClient,@Nonnull final LongId payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.suggestNewContact3(payload,sign(payload),new IntermediateResultHandler<>
						                                                     (intermediateResultEnqueuer,com.kareebo.contacts.client.processor
							                                                                                 .SuggestNewContact.method3,finalResultEnqueuer,method3,context));
				}
			}
		};
	}
}
