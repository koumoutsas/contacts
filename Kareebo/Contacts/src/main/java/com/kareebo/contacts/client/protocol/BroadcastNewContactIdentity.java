package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;

/**
 * Client-side implementation of the broadcast new contact identity service
 */
public class BroadcastNewContactIdentity extends Service<com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient>
{
	public static final String serviceName=BroadcastNewContactIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");
	public final static ServiceMethod method4=new ServiceMethod(serviceName,"4");
	final static ServiceMethod method5=new ServiceMethod(serviceName,"5");
	private final static ServiceMethod[] methods={method1,method2,method3,method4,method5};

	BroadcastNewContactIdentity(@Nonnull final Context context,@Nonnull final TAsyncClientManager asyncClientManager,@Nonnull final SigningKey signingKey,@Nonnull final ClientId clientId)
	{
		super(context,asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient construct(@Nonnull final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient(asyncClientManager);
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
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,@Nonnull final LongId payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity1(payload,sign(payload),new IntermediateResultHandler<>
						                                                               (intermediateResultEnqueuer,com.kareebo.contacts.client
							                                                                                           .processor.BroadcastNewContactIdentity.method1,finalResultEnqueuer,method1,context));
				}
			},
			new Functor<EncryptedBufferPairSet>()
			{
				@Override
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,@Nonnull final EncryptedBufferPairSet payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity2(payload,sign(payload),new IntermediateResultHandler<>(intermediateResultEnqueuer,com.kareebo.contacts.client
						                                                                                                                          .processor.BroadcastNewContactIdentity.method2,finalResultEnqueuer,method2,context));
				}
			},
			new Functor<SetEncryptedBuffer>()
			{
				@Override
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,@Nonnull final SetEncryptedBuffer payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity3(sign(payload.getBufferSet(),EncryptedBufferSigned::new)
						,new
							 IntermediateVoidResultHandler
							 (finalResultEnqueuer,
								 method3));
				}
			},
			new Functor<LongId>()
			{
				@Override
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,@Nonnull final LongId payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity4(payload,sign(payload),new
						                                                               IntermediateResultHandler<>
						                                                               (intermediateResultEnqueuer,com.kareebo.contacts.client
							                                                                                           .processor
							                                                                                           .BroadcastNewContactIdentity.method4,finalResultEnqueuer,method4,context));
				}
			},
			new Functor<HashBufferPair>()
			{
				@Override
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,@Nonnull final HashBufferPair payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity5(payload,sign(payload),new FinalResultHandler(finalResultEnqueuer,method5));
				}
			}
		};
	}
}
