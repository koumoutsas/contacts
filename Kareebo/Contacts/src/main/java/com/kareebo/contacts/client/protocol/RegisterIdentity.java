package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.RegisterIdentityInput;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

/**
 * Client-side implementation of the register identity service
 */
class RegisterIdentity extends Service<com.kareebo.contacts.thrift.RegisterIdentity.VertxClient>
{
	public static final String serviceName=RegisterIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");
	private final static ServiceMethod[] methods={method1,method2,method3};

	RegisterIdentity(final Context context,final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(context,asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.RegisterIdentity.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.RegisterIdentity.VertxClient(asyncClientManager);
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
			new Functor<HashBuffer>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.RegisterIdentity.VertxClient asyncClient,final HashBuffer payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.registerIdentity1(payload,sign
						                                      (payload),new IntermediateResultHandler<>(intermediateResultEnqueuer,
							                                                                               com.kareebo.contacts.client.processor.RegisterIdentity.method1,
							                                                                               finalResultEnqueuer,method1,context
					));
				}
			},
			new Functor<LongId>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.RegisterIdentity.VertxClient asyncClient,final LongId payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.registerIdentity2(payload
						                              .getId(),new
							                                       IntermediateResultHandler<>(intermediateResultEnqueuer,com.kareebo.contacts
								                                                                                              .client.processor.RegisterIdentity.method2,finalResultEnqueuer,method2,context));
				}
			},
			new Functor<RegisterIdentityInput>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.RegisterIdentity.VertxClient asyncClient,final RegisterIdentityInput payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.registerIdentity3(payload,sign
						                                      (payload),new FinalResultHandler(finalResultEnqueuer,method3));
				}
			}
		};
	}
}
