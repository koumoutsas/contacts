package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
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
	protected void runInternal(final ServiceMethod method,final TBase payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
	{
		if(method.equals(method1))
		{
			broadcastNewContactIdentity1((LongId)payload,intermediateResultEnqueuer,finalResultEnqueuer);
		}
		else if(method.equals(method2))
		{
			broadcastNewContactIdentity2((EncryptedBufferPairSet)payload,intermediateResultEnqueuer,finalResultEnqueuer);
		}
		else if(method.equals(method3))
		{
			broadcastNewContactIdentity3((SetEncryptedBuffer)payload,finalResultEnqueuer);
		}
		else if(method.equals(method4))
		{
			broadcastNewContactIdentity4((LongId)payload,intermediateResultEnqueuer,finalResultEnqueuer);
		}
		else if(method.equals(method5))
		{
			broadcastNewContactIdentity5((HashBufferPair)payload,finalResultEnqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void broadcastNewContactIdentity1(final LongId userIdB,final IntermediateResultEnqueuer intermediateResultEnqueuer,final ErrorEnqueuer errorEnqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.broadcastNewContactIdentity1(userIdB,sign(userIdB),new IntermediateResultHandler<MapClientIdEncryptionKey>(intermediateResultEnqueuer,errorEnqueuer,com.kareebo.contacts.client
			                                                                                                                                                                .processor.BroadcastNewContactIdentity.method1));
	}

	private void broadcastNewContactIdentity2(final EncryptedBufferPairSet encryptedBufferPairs,final IntermediateResultEnqueuer
		                                                                                            intermediateResultEnqueuer,final ErrorEnqueuer errorEnqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.broadcastNewContactIdentity2(encryptedBufferPairs,sign(encryptedBufferPairs),new
			                                                                                         IntermediateResultHandler<MapClientIdEncryptionKey>(intermediateResultEnqueuer,errorEnqueuer,com.kareebo.contacts.client
				                                                                                                                                                                                      .processor.BroadcastNewContactIdentity.method2));
	}

	private void broadcastNewContactIdentity3(final SetEncryptedBuffer encryptedBuffers,final ErrorEnqueuer errorEnqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBuffer> encryptedBuffersSet=encryptedBuffers.getBufferSet();
		final Set<EncryptedBufferSigned> set=new HashSet<>(encryptedBuffersSet.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBuffersSet)
		{
			set.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		asyncClient.broadcastNewContactIdentity3(set,new IntermediateVoidResultHandler(errorEnqueuer,com.kareebo.contacts.client
			                                                                                             .processor.BroadcastNewContactIdentity.method3));
	}

	private void broadcastNewContactIdentity4(final LongId id,final IntermediateResultEnqueuer
		                                                          intermediateResultEnqueuer,final ErrorEnqueuer errorEnqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.broadcastNewContactIdentity4(id,sign(id),new IntermediateResultHandler<EncryptedBufferSignedWithVerificationKey>
			                                                     (intermediateResultEnqueuer,errorEnqueuer,com.kareebo.contacts.client
				                                                                                               .processor.BroadcastNewContactIdentity.method4));
	}

	private void broadcastNewContactIdentity5(final HashBufferPair uCs,final FinalResultEnqueuer enqueuer) throws InvalidKeyException, TException,
		                                                                                                              NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.broadcastNewContactIdentity5(uCs,sign(uCs),new FinalResultHandler(enqueuer,method5));
	}
}
