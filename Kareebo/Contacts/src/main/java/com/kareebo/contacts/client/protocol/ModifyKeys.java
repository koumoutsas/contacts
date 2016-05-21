package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.PublicKeys;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

/**
 * Client-side implementation of the modify keys service
 */
class ModifyKeys extends Service<com.kareebo.contacts.thrift.ModifyKeys.VertxClient>
{
	public static final String serviceName=ModifyKeys.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	private final static ServiceMethod[] methodNames={method1};

	ModifyKeys(final Context context,final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(context,asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.ModifyKeys.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.ModifyKeys.VertxClient(asyncClientManager);
	}

	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return methodNames;
	}

	@Override
	protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
	{
		return new com.kareebo.contacts.client.jobs.Service.Functor[]{
			new Functor<PublicKeys>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.ModifyKeys.VertxClient asyncClient,final PublicKeys payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.modifyKeys1(payload,sign(payload),new FinalResultHandler(finalResultEnqueuer,method1));
				}
			}};
	}
}
