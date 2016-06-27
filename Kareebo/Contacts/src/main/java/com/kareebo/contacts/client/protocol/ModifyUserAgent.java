package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.UserAgent;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;

/**
 * Client-side implementation of the modify user agent service
 */
public class ModifyUserAgent extends Service<com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient>
{
	public static final String serviceName=ModifyUserAgent.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	private final static ServiceMethod[] methods={method1};

	ModifyUserAgent(final @Nonnull Context context,final @Nonnull TAsyncClientManager asyncClientManager,final @Nonnull SigningKey signingKey,final
	@Nonnull ClientId
		                                                                                                                                          clientId)
	{
		super(context,asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient construct(@Nonnull final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient(asyncClientManager);
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
		return new com.kareebo.contacts.client.jobs.Service.Functor[]{new Functor<UserAgent>()
		{
			@Override
			protected void runInternal(@Nonnull final com.kareebo.contacts.thrift.ModifyUserAgent.VertxClient asyncClient,@Nonnull final UserAgent payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer) throws Exception
			{
				asyncClient.modifyUserAgent1(payload,sign(payload),new FinalResultHandler(finalResultEnqueuer,method1));
			}
		}};
	}
}
