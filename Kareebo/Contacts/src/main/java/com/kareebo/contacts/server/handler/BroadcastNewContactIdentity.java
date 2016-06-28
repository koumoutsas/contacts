package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.crypto.Utils;
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

import javax.annotation.Nonnull;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * Server-side service implementation of the broadcast new contact identity operation
 */
class BroadcastNewContactIdentity extends SignatureVerifierWithIdentityStoreAndNotifier implements com.kareebo.contacts.thrift
	                                                                                                   .BroadcastNewContactIdentity.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(BroadcastNewContactIdentity.class.getName());

	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The datastore
	 * @param identityDatastore The identity datastore
	 * @param clientNotifier    The client notifier interface
	 */
	BroadcastNewContactIdentity(final @Nonnull DataStore<Long,User> userDataStore,final @Nonnull DataStore<ByteBuffer,HashIdentity>
		                                                                              identityDatastore,@Nonnull final ClientNotifier clientNotifier)
	{
		super(userDataStore,identityDatastore,clientNotifier);
	}

	@Override
	public void broadcastNewContactIdentity1(final @Nonnull LongId userIdB,final @Nonnull SignatureBuffer signature,final @Nonnull Future<MapClientIdEncryptionKey>
		                                                                                                                future)
	{
		final MapClientIdEncryptionKey reply=new MapClientIdEncryptionKey();
		final Map<ClientId,EncryptionKey> replyMap=new HashMap<>();
		verify(userIdB,signature,new Reply<>(future,reply),(user,client)->
		{
			final long userId=userIdB.getId();
			final Map<CharSequence,Client> clients=clientDBAccessor.get(userId).getClients();
			for(final Map.Entry<CharSequence,Client> entry : clients.entrySet())
			{
				try
				{
					replyMap.put(new ClientId(userId,TypeConverter.convert(entry.getKey())),TypeConverter.convert(entry
						                                                                                              .getValue().getKeys()
						                                                                                              .getEncryption()));
				}
				catch(NoSuchAlgorithmException e)
				{
					logger.error("Invalid algorithm retrieved from the datastore",e);
					throw new FailedOperation();
				}
			}
			reply.setKeyMap(replyMap);
		});
	}

	@Override
	public void broadcastNewContactIdentity2(final @Nonnull EncryptedBufferPairSet encryptedBufferPairs,final @Nonnull SignatureBuffer signature,final
	Future<MapClientIdEncryptionKey> future)
	{
		final Set<EncryptedBufferPair> encryptedBufferPairsSet=encryptedBufferPairs.getEncryptedBufferPairs();
		final MapClientIdEncryptionKey reply=new MapClientIdEncryptionKey();
		final Map<ClientId,EncryptionKey> replyMap=new HashMap<>(encryptedBufferPairsSet.size());
		verify(encryptedBufferPairs,signature,new Reply<>(future,reply),(user,client)->
		{
			try
			{
				final Utils.RSANoPaddingCipher rsaNoPaddingCipher=new Utils.RSANoPaddingCipher(client.getKeys().getEncryption());
				final BigInteger modulus=rsaNoPaddingCipher.publicKey.getModulus();
				final Cipher cipher=rsaNoPaddingCipher.cipher;
				for(final EncryptedBufferPair e : encryptedBufferPairsSet)
				{
					final ClientId clientIdB=e.getI().getClient();
					try
					{
						final Client clientB=clientDBAccessor.get(clientIdB);
						final List<com.kareebo.contacts.server.gora.EncryptedBuffer> comparisonIdentities=clientB.getComparisonIdentities();
						for(final com.kareebo.contacts.server.gora.EncryptedBuffer c : comparisonIdentities)
						{
							if(new BigInteger(cipher.doFinal(com.kareebo.contacts.base
								                                 .Utils.getBytes(c.getBuffer()))).multiply(new BigInteger(e.getI().getBuffer())).mod(modulus)
								   .equals(new BigInteger(e.getIR().getBuffer())))
							{
								replyMap.put(clientIdB,TypeConverter.convert(clientB.getKeys().getEncryption()));
								break;
							}
						}
					}
					catch(final FailedOperation exception)
					{
						logger.error("Unable to find client "+clientIdB);
						throw exception;
					}
					catch(final NoSuchAlgorithmException|BadPaddingException|IllegalBlockSizeException exception)
					{
						logger.error("Encryption failed for "+clientIdB,exception);
						throw new FailedOperation();
					}
				}
			}
			catch(final Utils.UnsupportedAlgorithmException|InvalidKeyException|NoSuchAlgorithmException
				            |InvalidKeySpecException|NoSuchProviderException
				            |NoSuchPaddingException e)
			{
				logger.error("Encryption failed with exception",e);
				throw new FailedOperation();
			}
			reply.setKeyMap(replyMap);
		});
	}

	@Override
	public void broadcastNewContactIdentity3(final @Nonnull Set<EncryptedBufferSigned> encryptedBuffers,final @Nonnull Future<Void> future)
	{
		for(final EncryptedBufferSigned encryptedBufferSigned : encryptedBuffers)
		{
			final DefaultFutureResult<Void> result=new DefaultFutureResult<>();
			final EncryptedBuffer encryptedBuffer=encryptedBufferSigned.getEncryptedBuffer();
			verify(encryptedBuffer,encryptedBufferSigned.getSignature(),
				new Reply<>(result),(user,client)->
				{
					final ClientId clientIdB=encryptedBuffer.getClient();
					final Client clientB=clientDBAccessor.get(clientIdB);
					try
					{
						notifyClient(clientB.getDeviceToken(),new NotificationObject(com.kareebo.contacts.client
							                                                             .protocol
							                                                             .BroadcastNewContactIdentity.method4,new EncryptedBufferSignedWithVerificationKey
								                                                                                                  (encryptedBufferSigned,TypeConverter.convert(client.getKeys().getVerification()))));
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
	public void broadcastNewContactIdentity4(final @Nonnull LongId id,final @Nonnull SignatureBuffer signature,final @Nonnull Future<EncryptedBufferSignedWithVerificationKey> future)
	{
		forward(new EncryptedBufferSignedWithVerificationKey(),id,signature,future);
	}

	@Override
	public void broadcastNewContactIdentity5(final @Nonnull HashBufferPair uCs,final @Nonnull SignatureBuffer signature,final @Nonnull Future<Void> future)
	{
		verify(uCs,signature,new Reply<>(future),(user,client)->
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
			List<Long> confirmersSmall, confirmersBig;
			ByteBuffer keySmall, keyBig;
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
			valueBig.setConfirmers(new ArrayList<>(new HashSet<>(confirmersBig)));
			put(keyBig,valueBig);
			aliasTo(keySmall,keyBig);
		});
	}
}
