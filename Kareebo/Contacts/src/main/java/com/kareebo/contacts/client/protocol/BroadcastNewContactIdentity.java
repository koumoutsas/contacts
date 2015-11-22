package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.*;
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
class BroadcastNewContactIdentity extends Service<com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient>
{
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
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method0))
		{
			broadcastNewContactIdentity1((LongId)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method1))
		{
			broadcastNewContactIdentity2((EncryptedBufferPairSet)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method2))
		{
			broadcastNewContactIdentity3((SetEncryptedBuffer)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method3))
		{
			broadcastNewContactIdentity4((LongId)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method4))
		{
			broadcastNewContactIdentity5((HashBufferPair)payload,enqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void broadcastNewContactIdentity1(final LongId userIdB,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.broadcastNewContactIdentity1(userIdB,sign(userIdB),new IntermediateResultHandler<MapClientIdEncryptionKey>(enqueuer,com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method1));
	}

	private void broadcastNewContactIdentity2(final EncryptedBufferPairSet encryptedBufferPairs,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.broadcastNewContactIdentity2(encryptedBufferPairs,sign(encryptedBufferPairs),new IntermediateResultHandler<MapClientIdEncryptionKey>(enqueuer,com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method2));
	}

	private void broadcastNewContactIdentity3(final SetEncryptedBuffer encryptedBuffers,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBuffer> encryptedBuffersSet=encryptedBuffers.getBufferSet();
		final Set<EncryptedBufferSigned> set=new HashSet<>(encryptedBuffersSet.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBuffersSet)
		{
			set.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		asyncClient.broadcastNewContactIdentity3(set,new IntermediateVoidResultHandler(enqueuer,com.kareebo
			                                                                                        .contacts.base.service
			                                                                                        .BroadcastNewContactIdentity.method3));
	}

	private void broadcastNewContactIdentity4(final LongId id,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.broadcastNewContactIdentity4(id,sign(id),new IntermediateResultHandler<EncryptedBufferSignedWithVerificationKey>
			                                                     (enqueuer,com.kareebo.contacts.base.service
				                                                               .BroadcastNewContactIdentity.method4));
	}

	private void broadcastNewContactIdentity5(final HashBufferPair uCs,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.broadcastNewContactIdentity5(uCs,sign(uCs),new FinalResultHandler(enqueuer,com.kareebo.contacts.base.service
			                                                                                       .BroadcastNewContactIdentity
			                                                                                       .method5));
	}
}
