package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.HashBufferSet;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;

/**
 * Client-side implementation of the register unconfirmed identity service
 */
class RegisterUnconfirmedIdentity extends Service<com.kareebo.contacts.thrift.RegisterUnconfirmedIdentity.VertxClient>
{
	public static final String serviceName=RegisterUnconfirmedIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	private final static ServiceMethod[] methodNames={method1};

	RegisterUnconfirmedIdentity(final Context context,final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId
		                                                                                                                           clientId)
	{
		super(context,asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.RegisterUnconfirmedIdentity.VertxClient construct(@Nonnull final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.RegisterUnconfirmedIdentity.VertxClient(asyncClientManager);
	}

	@Nonnull
	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return methodNames;
	}

	@Nonnull
	@Override
	protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
	{
		return new com.kareebo.contacts.client.jobs.Service.Functor[]{
			new Functor<HashBufferSet>()
			{
				@Override
				protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.RegisterUnconfirmedIdentity.VertxClient asyncClient,@Nonnull final HashBufferSet payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.registerUnconfirmedIdentity1(payload,sign(payload),new FinalResultHandler(finalResultEnqueuer,method1));
				}
			}};
	}
}
