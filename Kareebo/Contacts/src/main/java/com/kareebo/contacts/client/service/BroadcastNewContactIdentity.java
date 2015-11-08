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
import java.util.Map;
import java.util.Set;

/**
 * Client-side implementation of the broadcast new contact identity service
 */
public class BroadcastNewContactIdentity extends Signer
{
	final private com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient vertxClient;

	public BroadcastNewContactIdentity(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient(asyncClientManager);
	}

	public void broadcastNewContactIdentity1(final LongId userIdB,final AsyncResultHandler<Map<ClientId,EncryptionKey>> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.broadcastNewContactIdentity1(userIdB,sign(userIdB),handler);
	}

	public void broadcastNewContactIdentity2(final EncryptedBufferPairSet encryptedBufferPairs,final AsyncResultHandler<Map<ClientId,
		                                                                                                                       EncryptionKey>> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.broadcastNewContactIdentity2(encryptedBufferPairs,sign(encryptedBufferPairs),handler);
	}

	public void broadcastNewContactIdentity3(final Set<EncryptedBuffer> encryptedBuffers,final AsyncResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBufferSigned> set=new HashSet<>(encryptedBuffers.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBuffers)
		{
			set.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		vertxClient.broadcastNewContactIdentity3(set,handler);
	}

	public void broadcastNewContactIdentity4(final LongId id,final AsyncResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.broadcastNewContactIdentity4(id,sign(id),handler);
	}

	public void broadcastNewContactIdentity5(final HashBufferPair uCs,final AsyncResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.broadcastNewContactIdentity5(uCs,sign(uCs),handler);
	}
}
