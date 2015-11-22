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
import java.util.Map;
import java.util.Set;

/**
 * Client-side implementation of the broadcast new contact identity service
 */
public class BroadcastNewContactIdentity extends Signer implements NotifiableService
{
	final private com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient vertxClient;

	public BroadcastNewContactIdentity(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(signingKey,clientId);
		vertxClient=new com.kareebo.contacts.thrift.BroadcastNewContactIdentity.VertxClient(asyncClientManager);
	}

	public void broadcastNewContactIdentity1(final LongId userIdB,final ResultHandler<Map<ClientId,EncryptionKey>> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.broadcastNewContactIdentity1(userIdB,sign(userIdB),new AsyncResultHandler<>(handler));
	}

	public void broadcastNewContactIdentity2(final EncryptedBufferPairSet encryptedBufferPairs,final ResultHandler<Map<ClientId,
		                                                                                                                  EncryptionKey>> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.broadcastNewContactIdentity2(encryptedBufferPairs,sign(encryptedBufferPairs),new AsyncResultHandler<>(handler));
	}

	public void broadcastNewContactIdentity3(final Set<EncryptedBuffer> encryptedBuffers,final ResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<EncryptedBufferSigned> set=new HashSet<>(encryptedBuffers.size());
		for(final EncryptedBuffer encryptedBuffer : encryptedBuffers)
		{
			set.add(new EncryptedBufferSigned(encryptedBuffer,sign(encryptedBuffer)));
		}
		vertxClient.broadcastNewContactIdentity3(set,new AsyncResultHandler<>(handler));
	}

	public void broadcastNewContactIdentity5(final HashBufferPair uCs,final ResultHandler<Void> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		vertxClient.broadcastNewContactIdentity5(uCs,sign(uCs),new AsyncResultHandler<>(handler));
	}

	@Override
	public void run(final NotificationMethod method,final long notificationId,final ResultHandler<TBase> handler) throws NoSuchMethod, NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method3))
		{
			broadcastNewContactIdentity4(notificationId,new AsyncResultConnector<EncryptedBufferSignedWithVerificationKey>(handler));
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void broadcastNewContactIdentity4(final long id,final AsyncResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final LongId longId=new LongId(id);
		vertxClient.broadcastNewContactIdentity4(longId,sign(longId),handler);
	}
}
