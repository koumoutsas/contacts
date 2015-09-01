package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.CollectionPlaintextSerializer;
import com.kareebo.contacts.base.LongPlaintextSerializer;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Server-side service implementation of the broadcast new contact identity operation
 */
public class BroadcastNewContactIdentity extends SignatureVerifier implements com.kareebo.contacts.thrift.BroadcastNewContactIdentity.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(BroadcastNewContactIdentity.class.getName());

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	BroadcastNewContactIdentity(final DataStore<Long,User> dataStore)
	{
		super(dataStore);
	}

	@Override
	public void broadcastNewContactIdentity1(final long userIdB,final SignatureBuffer signature,final Future<Map<ClientId,EncryptionKey>> future)
	{
		final Map<ClientId,EncryptionKey> reply=new HashMap<>();
		verify(new LongPlaintextSerializer(userIdB),signature,new Reply<>(future,reply),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				final Map<CharSequence,Client> clients=clientDBAccessor.get(userIdB).getClients();
				for(final Map.Entry<CharSequence,Client> entry : clients.entrySet())
				{
					try
					{
						reply.put(new ClientId(userIdB,TypeConverter.convert(entry.getKey())),TypeConverter.convert(entry
							                                                                                            .getValue().getKeys()
							                                                                                            .getEncryption()));
					}
					catch(NoSuchAlgorithmException e)
					{
						logger.error("Invalid algorithm retrieved from the datastore",e);
						throw new FailedOperation();
					}
				}
			}
		});
	}

	@Override
	public void broadcastNewContactIdentity2(final Set<EncryptedBufferPair> encryptedBufferPairs,final SignatureBuffer signature,final Future<Map<ClientId,EncryptionKey>> future)
	{
		final Map<ClientId,EncryptionKey> reply=new HashMap<>(encryptedBufferPairs.size());
		verify(new CollectionPlaintextSerializer<>(encryptedBufferPairs),signature,new Reply<>(future,reply),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				for(final EncryptedBufferPair e : encryptedBufferPairs)
				{
					final byte[] I=e.getI().getBuffer();
					final byte[] IR=e.getIR().getBuffer();
					final ClientId clientIdB=e.getI().getClient();
					try
					{
						final Client clientB=clientDBAccessor.get(clientIdB);
						final List<com.kareebo.contacts.server.gora.EncryptedBuffer> comparisonIdentities=clientB
							                                                                                  .getComparisonIdentities();
						for(final com.kareebo.contacts.server.gora.EncryptedBuffer c : comparisonIdentities)
						{
							final ByteBuffer b=c.getBuffer();
							b.rewind();
							final byte[] i=new byte[b.remaining()];
							b.get(i);
							if(Arrays.equals(IR,Utils.xor(i,I)))
							{
								reply.put(clientIdB,TypeConverter.convert(clientB.getKeys().getEncryption()));
								break;
							}
						}
					}
					catch(FailedOperation exception)
					{
						logger.error("Unable to find client "+clientIdB);
					}
					catch(NoSuchAlgorithmException exception)
					{
						logger.error("Invalid algorithm found for "+clientIdB,exception);
					}
				}
			}
		});
	}

	@Override
	public void broadcastNewContactIdentity3(final Set<EncryptedBufferSigned> encryptedBuffers,final Future<Void> future)
	{
	}

	@Override
	public void broadcastNewContactIdentity4(final SignedRandomNumber signature,final Future<EncryptedBufferSignedWithVerificationKey> future)
	{
	}

	@Override
	public void BroadcastNewContactIdentity5(final HashBuffer uC,final SignatureBuffer signature,final Future<Void> future)
	{
	}
}
