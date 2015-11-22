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
 * Client-side implementation of the send contact card service
 */
class SendContactCard extends Service<com.kareebo.contacts.thrift.SendContactCard.VertxClient>
{
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
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.SendContactCard.method0))
		{
			sendContactCard1((HashBuffer)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.SendContactCard.method1))
		{
			sendContactCard2((LongId)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.SendContactCard.method2))
		{
			sendContactCard3((SetEncryptedBuffer)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.SendContactCard.method3))
		{
			sendContactCard4((LongId)payload,enqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void sendContactCard1(final HashBuffer u,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException,
		                                                                                 NoSuchProviderException, SignatureException
	{
		asyncClient.sendContactCard1(u,sign(u),new IntermediateVoidResultHandler(enqueuer,com.kareebo.contacts.base.service.SendContactCard
			                                                                                  .method1));
	}

	private void sendContactCard2(final LongId id,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException,
		                                                                              NoSuchProviderException, SignatureException
	{
		asyncClient.sendContactCard2(id,sign(id),new IntermediateResultHandler<EncryptionKeys>(enqueuer,com.kareebo.contacts.base.service
			                                                                                                .SendContactCard.method2));
	}

	private void sendContactCard3(final SetEncryptedBuffer encryptedBuffers,final Enqueuer enqueuer) throws InvalidKeyException,
		                                                                                                        TException,
		                                                                                                        NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBuffer> encryptedBufferSet=encryptedBuffers.getBufferSet();
		final Set<EncryptedBufferSigned> encryptedBufferSignedSet=new HashSet<>(encryptedBufferSet.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBufferSet)
		{
			encryptedBufferSignedSet.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		asyncClient.sendContactCard3(encryptedBufferSignedSet,new IntermediateVoidResultHandler(enqueuer,com.kareebo.contacts.base.service
			                                                                                                 .SendContactCard.method3));
	}

	private void sendContactCard4(final LongId id,final Enqueuer enqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException,
		                                                                              NoSuchProviderException, SignatureException
	{
		asyncClient.sendContactCard4(id,sign(id),new IntermediateResultHandler<EncryptedBufferSignedWithVerificationKey>(enqueuer,com
			                                                                                                                          .kareebo.contacts.base.service.SendContactCard.method4));
	}
}
