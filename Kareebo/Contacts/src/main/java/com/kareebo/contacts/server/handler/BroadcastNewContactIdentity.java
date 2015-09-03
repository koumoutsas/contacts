package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.CollectionPlaintextSerializer;
import com.kareebo.contacts.base.EncryptedBufferPlaintextSerializer;
import com.kareebo.contacts.base.HashBufferPairPlaintextSerializer;
import com.kareebo.contacts.base.LongPlaintextSerializer;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
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

/**
 * Server-side service implementation of the broadcast new contact identity operation
 */
public class BroadcastNewContactIdentity extends SignatureVerifierWithIdentityStore implements com.kareebo.contacts.thrift.BroadcastNewContactIdentity.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(BroadcastNewContactIdentity.class.getName());

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	BroadcastNewContactIdentity(final DataStore<Long,User> dataStore,final DataStore<ByteBuffer,HashIdentity>
		                                                                 identityDatastore)
	{
		super(dataStore,identityDatastore);
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
		for(final EncryptedBufferSigned encryptedBufferSigned : encryptedBuffers)
		{
			final DefaultFutureResult<Void> result=new DefaultFutureResult<>();
			verify(new EncryptedBufferPlaintextSerializer(encryptedBufferSigned.getEncryptedBuffer()),encryptedBufferSigned.getSignature(),
				      new Reply<>(result),new After()
				{
					@Override
					public void run(final User user,final Client client) throws FailedOperation
					{
						try
						{
							new EncryptedBufferSignedWithVerificationKey(encryptedBufferSigned,TypeConverter.convert(client.getKeys().getVerification()));
							//TODO notifications here
						}
						catch(NoSuchAlgorithmException e)
						{
							logger.error("Unknown algorithm",e);
							throw new FailedOperation();
						}
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
	public void broadcastNewContactIdentity4(final SignedRandomNumber signature,final Future<EncryptedBufferSignedWithVerificationKey> future)
	{
		final EncryptedBufferSignedWithVerificationKey encryptedBufferSignedWithVerificationKey=new
			                                                                                        EncryptedBufferSignedWithVerificationKey();
		verify(new LongPlaintextSerializer(signature.getI()),signature.getSignature(),new Reply<>(future,encryptedBufferSignedWithVerificationKey),new After(){
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				//TODO retrieve the notification payload
				final EncryptedBufferSignedWithVerificationKey retrieved=null;
				encryptedBufferSignedWithVerificationKey.setEncryptedBufferSigned(retrieved.getEncryptedBufferSigned());
				encryptedBufferSignedWithVerificationKey.setVerificationKey(retrieved.getVerificationKey());
			}
		});
	}

	@Override
	public void BroadcastNewContactIdentity5(final HashBufferPair uCs,final SignatureBuffer signature,final Future<Void> future)
	{
		verify(new HashBufferPairPlaintextSerializer(uCs),signature,new Reply<>(future),new After(){
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				final HashBuffer uC=uCs.getUC();
				final ByteBuffer keyUC=uC.bufferForBuffer();
				final HashIdentityValue value1=get(keyUC);
				if(value1==null)
				{
					logger.error("Identity "+uC+" not found");
					throw new FailedOperation();
				}
				final HashBuffer uPrimeC=uCs.getUPrimeC();
				final ByteBuffer keyUPrimeC=uPrimeC.bufferForBuffer();
				final HashIdentityValue value2=get(keyUPrimeC);
				if(value2==null)
				{
					logger.error("Identity "+uPrimeC+" not found");
					throw new FailedOperation();
				}
				final List<Long> confirmers1=value1.getConfirmers();
				final List<Long> confirmers2=value2.getConfirmers();
				HashIdentityValue valueBig;
				List<Long> confirmersSmall,confirmersBig;
				ByteBuffer keySmall,keyBig;
				if(confirmers1.size()<confirmers2.size())
				{
					valueBig=value2;
					confirmersSmall=confirmers1;
					confirmersBig=confirmers2;
					keySmall=keyUC;
					keyBig=keyUPrimeC;
				}
				else if(confirmers2.size()<confirmers1.size())
				{
					valueBig=value1;
					confirmersSmall=confirmers2;
					confirmersBig=confirmers1;
					keySmall=keyUPrimeC;
					keyBig=keyUC;
				}
				else
				{
					final int comparison=keyUC.compareTo(keyUPrimeC);
					if(comparison<0)
					{
						valueBig=value2;
						confirmersSmall=confirmers1;
						confirmersBig=confirmers2;
						keySmall=keyUC;
						keyBig=keyUPrimeC;
					}
					else if(comparison>0)
					{
						valueBig=value1;
						confirmersSmall=confirmers2;
						confirmersBig=confirmers1;
						keySmall=keyUPrimeC;
						keyBig=keyUC;
					}
					else
					{
						logger.error("Both identities are the same: "+keyUC.toString());
						throw new FailedOperation();
					}
				}
				confirmersBig.addAll(confirmersSmall);
				valueBig.setConfirmers(confirmersBig);
				put(keyBig,valueBig);
				aliasTo(keySmall,keyBig);
			}
		});
	}
}
