package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.async.TAsyncClientManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Client-side implementation of the send contact card service
 */
public class SendContactCard extends Service<com.kareebo.contacts.thrift.SendContactCard.VertxClient>
{
	public static final String serviceName=SendContactCard.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");
	public final static ServiceMethod method4=new ServiceMethod(serviceName,"4");
	private final static ServiceMethod[] methods={method1,method2,method3,method4};

	SendContactCard(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.SendContactCard.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.SendContactCard.VertxClient(asyncClientManager);
	}

	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return methods;
	}

	@Override
	protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
	{
		return new com.kareebo.contacts.client.jobs.Service.Functor[]
			       {
				       new Functor<HashBuffer>()
				       {
					       @Override
					       protected void runInternal(final com.kareebo.contacts.thrift.SendContactCard.VertxClient asyncClient,final HashBuffer payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       asyncClient.sendContactCard1(payload,sign(payload),new IntermediateVoidResultHandler(finalResultEnqueuer,method1));
					       }
				       },
				       new Functor<LongId>()
				       {
					       @Override
					       protected void runInternal(final com.kareebo.contacts.thrift.SendContactCard.VertxClient asyncClient,final LongId payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       asyncClient.sendContactCard2(payload,sign(payload),new IntermediateResultHandler<EncryptionKeys>(intermediateResultEnqueuer,com.kareebo.contacts.client.processor
							                                                                                                                                   .SendContactCard.method2,finalResultEnqueuer,method2));
					       }
				       },
				       new Functor<SetEncryptedBuffer>()
				       {
					       @Override
					       protected void runInternal(final com.kareebo.contacts.thrift.SendContactCard.VertxClient asyncClient,final SetEncryptedBuffer payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       final Set<EncryptedBuffer> encryptedBufferSet=payload.getBufferSet();
						       final Set<EncryptedBufferSigned> encryptedBufferSignedSet=new HashSet<>(encryptedBufferSet.size());
						       for(final EncryptedBuffer encryptedBuffer : encryptedBufferSet)
						       {
							       encryptedBufferSignedSet.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
						       }
						       asyncClient.sendContactCard3(encryptedBufferSignedSet,new IntermediateVoidResultHandler(finalResultEnqueuer,method3));
					       }
				       },
				       new Functor<LongId>()
				       {
					       @Override
					       protected void runInternal(final com.kareebo.contacts.thrift.SendContactCard.VertxClient asyncClient,final LongId payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       asyncClient.sendContactCard4(payload,sign(payload),new IntermediateResultHandler<EncryptedBufferSignedWithVerificationKey>
							                                                          (intermediateResultEnqueuer,com.kareebo.contacts.client.processor.SendContactCard
								                                                                                      .method4,finalResultEnqueuer,method4));
					       }
				       }
			       };
	}
}