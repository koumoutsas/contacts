package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;

/**
 * Client-side implementation of the send contact card service
 */
public class SendContactCard extends Service<com.kareebo.contacts.thrift.SendContactCard.VertxClient>
{
	public static final String serviceName=SendContactCard.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");
	final static ServiceMethod method4=new ServiceMethod(serviceName,"4");
	private final static ServiceMethod[] methods={method1,method2,method3,method4};

	SendContactCard(@Nonnull final Context context,@Nonnull final TAsyncClientManager asyncClientManager,@Nonnull final SigningKey signingKey,@Nonnull final ClientId clientId)
	{
		super(context,asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.SendContactCard.VertxClient construct(@Nonnull final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.SendContactCard.VertxClient(asyncClientManager);
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
		return new com.kareebo.contacts.client.jobs.Service.Functor[]
			       {
				       new Functor<HashBuffer>()
				       {
					       @Override
					       protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.SendContactCard.VertxClient asyncClient,@Nonnull final HashBuffer payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       asyncClient.sendContactCard1(payload,sign(payload),new IntermediateVoidResultHandler(finalResultEnqueuer,method1));
					       }
				       },
				       new Functor<LongId>()
				       {
					       @Override
					       protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.SendContactCard.VertxClient asyncClient,@Nonnull final LongId payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       asyncClient.sendContactCard2(payload,sign(payload),new IntermediateResultHandler<>(intermediateResultEnqueuer,com.kareebo.contacts.client.processor
							                                                                                                                     .SendContactCard.method2,finalResultEnqueuer,method2,context));
					       }
				       },
				       new Functor<SetEncryptedBuffer>()
				       {
					       @Override
					       protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.SendContactCard.VertxClient asyncClient,@Nonnull final SetEncryptedBuffer payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       asyncClient.sendContactCard3(sign(payload.getBufferSet(),EncryptedBufferSigned::new),new
							                                                                                            IntermediateVoidResultHandler
							                                                                                            (finalResultEnqueuer,
								                                                                                            method3));
					       }
				       },
				       new Functor<LongId>()
				       {
					       @Override
					       protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.SendContactCard.VertxClient asyncClient,@Nonnull final LongId payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       asyncClient.sendContactCard4(payload,sign(payload),new IntermediateResultHandler<>
							                                                          (intermediateResultEnqueuer,com.kareebo.contacts.client.processor.SendContactCard
								                                                                                      .method4,finalResultEnqueuer,method4,context));
					       }
				       }
			       };
	}
}
