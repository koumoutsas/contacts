package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.async.TAsyncClientManager;

import java.util.HashSet;
import java.util.Set;

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
	public final static ServiceMethod method5=new ServiceMethod(serviceName,"5");
	private final static ServiceMethod[] methods={method1,method2,method3,method4,method5};

	BroadcastNewContactIdentity(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient(asyncClientManager);
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
				protected void runInternal(final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,final LongId payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity1(payload,sign(payload),new IntermediateResultHandler<MapClientIdEncryptionKey>
						                                                               (intermediateResultEnqueuer,com.kareebo.contacts.client
							                                                                                           .processor.BroadcastNewContactIdentity.method1,finalResultEnqueuer,method1));
				}
			},
			new Functor<EncryptedBufferPairSet>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,final EncryptedBufferPairSet payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity2(payload,sign(payload),new IntermediateResultHandler<MapClientIdEncryptionKey>(intermediateResultEnqueuer,com.kareebo.contacts.client
						                                                                                                                                                  .processor.BroadcastNewContactIdentity.method2,finalResultEnqueuer,method2));
				}
			},
			new Functor<SetEncryptedBuffer>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,final SetEncryptedBuffer payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					final Set<EncryptedBuffer> encryptedBuffersSet=payload.getBufferSet();
					final Set<EncryptedBufferSigned> set=new HashSet<>(encryptedBuffersSet.size());
					for(final EncryptedBuffer encryptedBuffer : encryptedBuffersSet)
					{
						set.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
					}
					asyncClient.broadcastNewContactIdentity3(set,new IntermediateVoidResultHandler(finalResultEnqueuer,method3));
				}
			},
			new Functor<LongId>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,final LongId payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity4(payload,sign(payload),new
						                                                               IntermediateResultHandler<EncryptedBufferSignedWithVerificationKey>
						                                                               (intermediateResultEnqueuer,com.kareebo.contacts.client
							                                                                                           .processor
							                                                                                           .BroadcastNewContactIdentity.method4,finalResultEnqueuer,method4));
				}
			},
			new Functor<HashBufferPair>()
			{
				@Override
				protected void runInternal(final com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient asyncClient,final HashBufferPair payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					asyncClient.broadcastNewContactIdentity5(payload,sign(payload),new FinalResultHandler(finalResultEnqueuer,method5));
				}
			}
		};
	}
}
