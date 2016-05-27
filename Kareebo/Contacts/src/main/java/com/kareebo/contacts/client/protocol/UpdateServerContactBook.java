package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.ContactOperationSet;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;

/**
 * Client-side implementation of the update server contact book service.
 */
class UpdateServerContactBook extends Service<com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient>
{
	public static final String serviceName=UpdateServerContactBook.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	private final static ServiceMethod[] methodNames={method1};

	UpdateServerContactBook(final Context context,final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId
		                                                                                                                       clientId)
	{
		super(context,asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient(asyncClientManager);
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
			new Functor<ContactOperationSet>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.UpdateServerContactBook.VertxClient asyncClient,final ContactOperationSet payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.updateServerContactBook1(payload,sign(payload),new FinalResultHandler(finalResultEnqueuer,
						                                                                                 method1));
				}
			}};
	}
}
