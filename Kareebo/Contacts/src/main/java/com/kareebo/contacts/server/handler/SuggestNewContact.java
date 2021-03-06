package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Service implementation for updating the sending a contact card
 */
class SuggestNewContact extends SignatureVerifierWithIdentityStoreAndNotifier implements com.kareebo.contacts.thrift.SuggestNewContact.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(SuggestNewContact.class.getName());

	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The user datastore
	 * @param identityDatastore The identity datastore
	 * @param clientNotifier    The client notifier interface
	 */
	SuggestNewContact(final @Nonnull DataStore<Long,User> userDataStore,final @Nonnull DataStore<ByteBuffer,HashIdentity> identityDatastore,final @Nonnull ClientNotifier clientNotifier)
	{
		super(userDataStore,identityDatastore,clientNotifier);
	}

	@Override
	public void suggestNewContact1(final @Nonnull LongId id,final @Nonnull SignatureBuffer signature,final @Nonnull Future<EncryptionKeysWithHashBuffer> future)
	{
		final EncryptionKeysWithHashBuffer reply=new EncryptionKeysWithHashBuffer();
		verify(id,signature,new Reply<>(future,reply),(user,client)->{
			final HashBuffer uB=new HashBuffer();
			get(uB,id.getId());
			reply.setU(uB);
			final Long userBId=get(uB.bufferForBuffer()).getId();
			final Map<CharSequence,Client> clientsB=clientDBAccessor.get(userBId).getClients();
			final Map<Long,EncryptionKey> keys=new HashMap<>(clientsB.size());
			for(Map.Entry<CharSequence,Client> entry : clientsB.entrySet())
			{
				try
				{
					keys.put(TypeConverter.convert(entry.getKey()),TypeConverter.convert(entry.getValue().getKeys()
						                                                                     .getEncryption()));
				}
				catch(NoSuchAlgorithmException e)
				{
					logger.error("Invalid algorithm retrieved from the datastore",e);
					throw new FailedOperation();
				}
			}
			reply.setEncryptionKeys(new EncryptionKeys(userBId,keys));
		});
	}

	@Override
	public void suggestNewContact2(final @Nonnull Set<EncryptedBufferSigned> encryptedBuffers,final @Nonnull HashBuffer uB,final @Nonnull SignatureBuffer signature,final @Nonnull Future<Void> future)
	{
		verify(uB,signature,new Reply<>(future),(user,client)->{
			// Declared this early to allow the conversion exception to be caught in the first catch block, for 100% coverage.
			// It is not possible for a conversion exception to be triggered, after the first verification has passed
			final VerificationKey verificationKey;
			try
			{
				final List<com.kareebo.contacts.server.gora.HashBuffer> sentRequests=user.getSentRequests();
				final com.kareebo.contacts.server.gora.HashBuffer uBConverted=TypeConverter.convert(uB);
				if(sentRequests.stream().anyMatch(c->Arrays.equals(uB.getBuffer(),Utils.getBytes(c.getBuffer()))))
				{
					return;
				}
				sentRequests.add(uBConverted);
				user.setSentRequests(sentRequests);
				verificationKey=TypeConverter.convert(client.getKeys().getVerification());
			}
			catch(NoSuchAlgorithmException e)
			{
				logger.error("Invalid algorithm retrieved from the datastore",e);
				throw new FailedOperation();
			}
			for(EncryptedBufferSigned e : encryptedBuffers)
			{
				final Reply reply=new Reply<>(future);
				verify(e.getEncryptedBuffer(),e.getSignature(),reply,(user1,client1)->{
				});
				if(reply.failed())
				{
					throw new FailedOperation();
				}
			}
			final Map<Long,NotificationObject> notifications=new HashMap<>(encryptedBuffers.size());
			for(EncryptedBufferSigned e : encryptedBuffers)
			{
				notifications.put(clientDBAccessor.get(e.getEncryptedBuffer().getClient()).getDeviceToken(),new
					                                                                                            NotificationObject(com.kareebo.contacts.client.protocol.SuggestNewContact.method2,new EncryptedBufferSignedWithVerificationKey(e,verificationKey)));
			}
			notifyClients(notifications);
		});
	}

	@Override
	public void suggestNewContact3(final @Nonnull LongId id,final @Nonnull SignatureBuffer signature,final @Nonnull Future<EncryptedBufferSignedWithVerificationKey> future)
	{
		forward(new EncryptedBufferSignedWithVerificationKey(),id,signature,future);
	}
}
