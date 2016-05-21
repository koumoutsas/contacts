package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Server-side service implementation of the send contact card operation
 */
class SendContactCard extends SignatureVerifierWithIdentityStoreAndNotifier implements com.kareebo.contacts.thrift.SendContactCard.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(SendContactCard.class.getName());

	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The user datastore
	 * @param identityDatastore The identity datastore
	 * @param clientNotifier    The client notifier interface
	 */
	SendContactCard(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
	{
		super(userDataStore,identityDatastore,clientNotifier);
	}

	@Override
	public void sendContactCard1(final HashBuffer u,final SignatureBuffer signature,final Future<Void> future)
	{
		verify(u,signature,new Reply<>(future),(user,client)->{
			final Map<CharSequence,Client> clientsA=user.getClients();
			final Map<Long,EncryptionKey> encryptionKeyMap=new HashMap<>(clientsA.size());
			for(final Object o : clientsA.entrySet())
			{
				final Map.Entry entry=(Map.Entry)o;
				try
				{
					encryptionKeyMap.put(TypeConverter.convert((CharSequence)entry.getKey()),TypeConverter.convert(((Client)entry
						                                                                                                        .getValue())
						                                                                                               .getKeys
							                                                                                                ().getEncryption
								                                                                                                   ()));
				}
				catch(NoSuchAlgorithmException e)
				{
					logger.error("Unknown algorithm",e);
					throw new FailedOperation();
				}
			}
			final EncryptionKeys encryptionKeys=new EncryptionKeys(user.getId(),encryptionKeyMap);
			final Collection<Client> clientsB=clientDBAccessor.get(find(u.bufferForBuffer())).getClients().values();
			final List<Long> deviceTokens=new ArrayList<>(clientsB.size());
			deviceTokens.addAll(clientsB.stream().map(Client::getDeviceToken).collect(Collectors.toList()));
			notifyClients(deviceTokens,new NotificationObject(com.kareebo.contacts.client.protocol.SendContactCard.method1,
				                                                 encryptionKeys));
		});
	}

	@Override
	public void sendContactCard2(final LongId id,final SignatureBuffer signature,final Future<EncryptionKeys> future)
	{
		forward(new EncryptionKeys(),id,signature,future);
	}

	@Override
	public void sendContactCard3(final Set<EncryptedBufferSigned> encryptedBuffers,final Future<Void> future)
	{
		for(final EncryptedBufferSigned encryptedBufferSigned : encryptedBuffers)
		{
			final DefaultFutureResult<Void> result=new DefaultFutureResult<>();
			final EncryptedBuffer encryptedBuffer=encryptedBufferSigned.getEncryptedBuffer();
			verify(encryptedBuffer,encryptedBufferSigned.getSignature(),new Reply<>(result),(user,client)->{
				try
				{
					notifyClient(clientDBAccessor.get(encryptedBuffer.getClient()).getDeviceToken(),new
						                                                                                NotificationObject(com.kareebo.contacts.client.protocol.SendContactCard.method3,
							                                                                                                  new EncryptedBufferSignedWithVerificationKey(encryptedBufferSigned,TypeConverter.convert(client.getKeys().getVerification()))));
				}
				catch(NoSuchAlgorithmException e)
				{
					logger.error("Unknown algorithm",e);
					throw new FailedOperation();
				}
			});
			if(result.failed())
			{
				future.setFailure(new FailedOperation());
				return;
			}
		}
		future.setResult(null);
	}

	@Override
	public void sendContactCard4(final LongId id,final SignatureBuffer signature,final Future<EncryptedBufferSignedWithVerificationKey> future)
	{
		forward(new EncryptedBufferSignedWithVerificationKey(),id,signature,future);
	}
}
