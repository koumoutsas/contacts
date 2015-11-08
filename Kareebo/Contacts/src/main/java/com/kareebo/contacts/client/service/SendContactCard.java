package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
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
public class SendContactCard extends Signer
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

	public void sendContactCard2(final LongId id,final AsyncResultHandler<EncryptionKeys> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.sendContactCard2(id,sign(id),handler);
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

	public void sendContactCard4(final LongId id,final AsyncResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.sendContactCard4(id,sign(id),handler);
	}
}
