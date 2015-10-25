package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Service implementation for updating the sending a contact card
 */
public class SuggestNewContact extends SignatureVerifierWithIdentityStoreAndNotifier implements com.kareebo.contacts.thrift.SuggestNewContact.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(SuggestNewContact.class.getName());

	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The user datastore
	 * @param identityDatastore The identity datastore
	 * @param clientNotifier    The client notifier interface
	 */
	SuggestNewContact(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
	{
		super(userDataStore,identityDatastore,clientNotifier);
	}

	@Override
	public void suggestNewContact1(final LongId id,final SignatureBuffer signature,final Future<EncryptionKeysWithHashBuffer> future)
	{
		final EncryptionKeysWithHashBuffer reply=new EncryptionKeysWithHashBuffer();
		verify(id,signature,new Reply<>(future,reply),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
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
			}
		});
	}

	@Override
	public void suggestNewContact2(final Set<EncryptedBufferSigned> encryptedBuffers,final HashBuffer uB,final SignatureBuffer signature,final Future<Void> future)
	{
		verify(uB,signature,new Reply<>(future),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				// Declared this early to allow the conversion exception to be caught in the first catch block, for 100% coverage.
				// It is not possible for a conversion exception to be triggered, after the first verification has passed
				final VerificationKey verificationKey;
				try
				{
					final List<com.kareebo.contacts.server.gora.HashBuffer> sentRequests=user.getSentRequests();
					final com.kareebo.contacts.server.gora.HashBuffer uBConverted=TypeConverter.convert(uB);
					for(final com.kareebo.contacts.server.gora.HashBuffer c : sentRequests)
					{
						if(Arrays.equals(uB.getBuffer(),Utils.getBytes(c.getBuffer())))
						{
							return;
						}
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
					verify(e.getEncryptedBuffer(),e.getSignature(),reply,new After()
					{
						@Override
						public void run(final User user,final Client client) throws FailedOperation
						{
						}
					});
					if(reply.failed())
					{
						throw new FailedOperation();
					}
				}
				final Map<Long,TBase> notifications=new HashMap<>(encryptedBuffers.size());
				for(EncryptedBufferSigned e : encryptedBuffers)
				{
					notifications.put(clientDBAccessor.get(e.getEncryptedBuffer().getClient()).getDeviceToken(),new EncryptedBufferSignedWithVerificationKey(e,verificationKey));
				}
				notifyClients(notifications);
			}
		});
	}

	@Override
	public void suggestNewContact3(final LongId id,final SignatureBuffer signature,final Future<EncryptedBufferSignedWithVerificationKey> future)
	{
		forward(new EncryptedBufferSignedWithVerificationKey(),id,signature,future);
	}
}
