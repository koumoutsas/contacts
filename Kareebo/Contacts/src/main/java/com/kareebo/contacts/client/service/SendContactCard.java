package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
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
public class SendContactCard extends Signer implements Service
{
	final private com.kareebo.contacts.thrift.SendContactCard.VertxClient vertxClient;

	public SendContactCard(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.SendContactCard.VertxClient(asyncClientManager);
	}

	public void sendContactCard1(final HashBuffer u,final AsyncResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.sendContactCard1(u,sign(u),handler);
	}

	public void sendContactCard3(final Set<EncryptedBuffer> encryptedBuffers,final AsyncResultHandler<Void> handler) throws InvalidKeyException,
		                                                                                                                        TException,
		                                                                                                                        NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBufferSigned> encryptedBufferSignedSet=new HashSet<>(encryptedBuffers.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBuffers)
		{
			encryptedBufferSignedSet.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		vertxClient.sendContactCard3(encryptedBufferSignedSet,handler);
	}

	@Override
	public void run(final NotificationMethod method,final long notificationId,final AsyncResultHandler<TBase> handler) throws NoSuchMethod, NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		if(method.equals(com.kareebo.contacts.base.service.SendContactCard.method1))
		{
			sendContactCard2(notificationId,new AsyncResultConnector<EncryptionKeys>(handler));
		}
		else if(method.equals(com.kareebo.contacts.base.service.SendContactCard.method3))
		{
			sendContactCard4(notificationId,new AsyncResultConnector<EncryptedBufferSignedWithVerificationKey>(handler));
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void sendContactCard2(final long id,final AsyncResultHandler<EncryptionKeys> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final LongId longId=new LongId(id);
		vertxClient.sendContactCard2(longId,sign(longId),handler);
	}

	private void sendContactCard4(final long id,final AsyncResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final LongId longId=new LongId(id);
		vertxClient.sendContactCard4(longId,sign(longId),handler);
	}
}
