package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.LongPlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	public void broadcastNewContactIdentity1(final long userIdB,final SignatureBuffer signature,final Future<Map<Long,EncryptionKey>> future)
	{
		final Map<Long,EncryptionKey> reply=new HashMap<>();
		verify(new LongPlaintextSerializer(userIdB),signature,future,new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				final Map<CharSequence,Client> clients=clientDBAccessor.get(userIdB).getClients();
				for(final Map.Entry<CharSequence,Client> entry : clients.entrySet())
				{
					try
					{
						reply.put(TypeConverter.convert(entry.getKey()),TypeConverter.convert(entry.getValue().getKeys()
							                                                                      .getEncryption()));
					}
					catch(NoSuchAlgorithmException e)
					{
						logger.error("Invalid algorithm retrieved from the datastore: "+e.toString());
						throw new FailedOperation();
					}
				}
			}
		});
		if(future.succeeded())
		{
			future.setResult(reply);
		}
	}

	@Override
	public void broadcastNewContactIdentity2(final Set<EncryptedBufferPair> encryptedBufferPairs,final SignatureBuffer signature,final Future<Set<EncryptedBuffer>> future)
	{
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
