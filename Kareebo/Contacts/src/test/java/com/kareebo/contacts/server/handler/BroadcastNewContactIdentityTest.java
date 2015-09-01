package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.CollectionPlaintextSerializer;
import com.kareebo.contacts.base.LongPlaintextSerializer;
import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.server.gora.EncryptedBuffer;
import com.kareebo.contacts.server.gora.HashBuffer;
import com.kareebo.contacts.server.gora.PublicKeys;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.UserAgent;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;
import org.apache.gora.store.DataStore;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit test for {@link BroadcastNewContactIdentity}
 */
public class BroadcastNewContactIdentityTest
{
	/**
	 * Create a user (10), create four clients (0,1,2,3) and set an encryption key for each of them.
	 * 0: Create I and IR that match
	 * 1: Create I and IR that match
	 * 2: Don't create I and IR for it
	 * 3: Create I and IR that match, but the comparison identity has invalid algorithm
	 * 4: Create an I and IR for client 4 that doesn't match to any client in the datastore
	 * Execute call 2 and expect success
	 */
	@Test
	public void testBroadcastNewContactIdentity2() throws Exception
	{
		class Base2 extends Base
		{
			final User user=new User();
			private final Set<ClientId> expected=new HashSet<>(2);
			private final Set<EncryptedBufferPair> encryptedBufferPairs=new HashSet<>(4);

			private Base2() throws Exception
			{
				setupDatastores();
				setUp();
				dataStore.put(user.getId(),user);
			}

			/**
			 * Set up the datastores, make call 2 and check the results
			 */
			void run() throws NoSuchAlgorithmException
			{
				final Future<Map<ClientId,EncryptionKey>> future=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity2(encryptedBufferPairs,signature,future);
				check(future);
			}			@Override
			PlaintextSerializer constructPlaintext()
			{
				return new CollectionPlaintextSerializer<>(encryptedBufferPairs);
			}

			/**
			 * Check the result
			 *
			 * @param future The result to be checked
			 */
			private void check(final Future<Map<ClientId,EncryptionKey>> future) throws NoSuchAlgorithmException
			{
				assertTrue(future.succeeded());
				final Map<ClientId,EncryptionKey> reply=future.result();
				assertEquals(expected.size(),reply.size());
				for(final ClientId clientId : expected)
				{
					final EncryptionKey encryptionKey=reply.get(clientId);
					assertNotNull(encryptionKey);
					final EncryptionKey expectedEncryptionKey=TypeConverter.convert(dataStore.get(clientId.getUser()).getClients().get
						                                                                                                               (TypeConverter.convert
							                                                                                                                              (clientId.getClient())).getKeys().getEncryption());
					assertEquals(expectedEncryptionKey,encryptionKey);
				}
			}

			/**
			 * Creates a client and sets the first comparison identity to a random 3-byte number. Use the first comparison identity to
			 * create the encrypted buffer pair
			 *
			 * @param id        The client id
			 * @param clients   The client set
			 * @param algorithm The algorithm used for the encryption key
			 * @return The created client
			 */
			private Client setupClient(final ClientId id,final HashMap<CharSequence,Client> clients,final com.kareebo.contacts.server
				                                                                                              .gora
				                                                                                              .EncryptionAlgorithm algorithm)
			{
				final Client client=new Client();
				final UserAgent userAgent=new UserAgent();
				userAgent.setPlatform("A");
				userAgent.setVersion("B");
				client.setUserAgent(userAgent);
				final com.kareebo.contacts.server.gora.EncryptionKey encryptionKey=new com.kareebo.contacts.server.gora
					                                                                       .EncryptionKey();
				encryptionKey.setAlgorithm(algorithm);
				final byte[] bytes=new byte[3];
				new SecureRandom().nextBytes(bytes);
				final ByteBuffer b=ByteBuffer.wrap(bytes);
				b.mark();
				encryptionKey.setBuffer(b);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.SHA256withECDSAprime239v1);
				verificationKey.setBuffer(b);
				final PublicKeys publicKeys=new PublicKeys();
				publicKeys.setEncryption(encryptionKey);
				publicKeys.setVerification(verificationKey);
				client.setKeys(publicKeys);
				final List<EncryptedBuffer> comparisonIdentities=new ArrayList<>();
				final EncryptedBuffer comparisonIdentity1=new EncryptedBuffer();
				comparisonIdentity1.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
				comparisonIdentity1.setBuffer(b);
				comparisonIdentities.add(comparisonIdentity1);
				final EncryptedBuffer comparisonIdentity2=new EncryptedBuffer();
				comparisonIdentity2.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
				final ByteBuffer c=ByteBuffer.wrap("12345".getBytes());
				c.mark();
				comparisonIdentity2.setBuffer(c);
				comparisonIdentities.add(comparisonIdentity2);
				client.setComparisonIdentities(comparisonIdentities);
				clients.put(TypeConverter.convert(id.getClient()),client);
				return client;
			}

			private Client setupValidClient(final ClientId id,final HashMap<CharSequence,Client> clients)
			{
				return setupClient(id,clients,com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
			}

			private void setupMatchingClient(final ClientId id,final HashMap<CharSequence,Client> clients,final boolean reverse)
			{
				expected.add(id);
				createEncryptedBufferPair(setupValidClient(id,clients),id,reverse);
			}

			private void createEncryptedBufferPair(final Client client,final ClientId id,final boolean reverse)
			{
				final List<EncryptedBuffer> comparisonIdentities=client.getComparisonIdentities();
				final ByteBuffer comparisonIdentity=comparisonIdentities.iterator().next().getBuffer();
				if(reverse)
				{
					Collections.reverse(comparisonIdentities);
				}
				comparisonIdentity.rewind();
				final byte[] b=new byte[comparisonIdentity.remaining()];
				comparisonIdentity.get(b);
				final byte[] i=new byte[b.length];
				new SecureRandom().nextBytes(i);
				final ByteBuffer iB=ByteBuffer.wrap(i);
				iB.mark();
				final com.kareebo.contacts.thrift.EncryptedBuffer I=new com.kareebo.contacts.thrift.EncryptedBuffer(iB,
					                                                                                                   EncryptionAlgorithm.RSA2048,id);
				final ByteBuffer iRB=ByteBuffer.wrap(Utils.xor(b,i));
				iRB.mark();
				final com.kareebo.contacts.thrift.EncryptedBuffer IR=new com.kareebo.contacts.thrift.EncryptedBuffer(iRB,
					                                                                                                    EncryptionAlgorithm.RSA2048,id);
				encryptedBufferPairs.add(new EncryptedBufferPair(I,IR));
			}

			private HashMap<CharSequence,Client> setupClients(final Long userId)
			{
				final HashMap<CharSequence,Client> ret=new HashMap<>(4);
				setupMatchingClient(new ClientId(userId,(long)0),ret,true);
				setupMatchingClient(new ClientId(userId,(long)1),ret,false);
				setupValidClient(new ClientId(userId,(long)2),ret);
				final ClientId id3=new ClientId(userId,(long)3);
				createEncryptedBufferPair(setupClient(id3,ret,com.kareebo.contacts.server.gora
					                                              .EncryptionAlgorithm.Fake),id3,false);
				final ByteBuffer byteBuffer=ByteBuffer.wrap("4".getBytes());
				byteBuffer.mark();
				final com.kareebo.contacts.thrift.EncryptedBuffer encryptedBuffer5=new com.kareebo.contacts.thrift.EncryptedBuffer
					                                                                   (byteBuffer,EncryptionAlgorithm.RSA2048,new ClientId(userId,4));
				encryptedBufferPairs.add(new EncryptedBufferPair(encryptedBuffer5,encryptedBuffer5));
				return ret;
			}

			/**
			 * Set up the datastores and and the encrypted buffer pairs
			 */
			private void setupDatastores()
			{
				user.setId((long)10);
				final ByteBuffer b=ByteBuffer.wrap("".getBytes());
				b.mark();
				user.setBlind(b);
				user.setClients(setupClients(user.getId()));
				user.setIdentities(new ArrayList<HashBuffer>());
				user.setSentRequests(new ArrayList<HashBuffer>());
			}


		}
		new Base2().run();
	}

	/**
	 * Create a user (0), create two clients (0, 1), set an encryption key for each of them and retrieve the encryption keys by user id 0
	 */
	@Test
	public void testBroadcastNewContactIdentity1() throws Exception
	{
		final Long i=(long)10;
		new Base1(i)
		{
			void run() throws NoSuchAlgorithmException
			{
				final Map<ClientId,EncryptionKey> expected=new HashMap<>(2);
				final User newUser=new User();
				newUser.setId(i);
				final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
				b.mark();
				newUser.setBlind(b);
				newUser.setIdentities(new ArrayList<HashBuffer>());
				newUser.setSentRequests(new ArrayList<HashBuffer>());
				final Map<CharSequence,Client> clients=new HashMap<>(expected.size());
				final Client client0=new Client();
				final List<EncryptedBuffer> comparisonIdentities=new ArrayList<>();
				client0.setComparisonIdentities(comparisonIdentities);
				client0.setUserAgent(userAgent);
				final EncryptionKey encryptionKey0=new EncryptionKey();
				encryptionKey0.setAlgorithm(EncryptionAlgorithm.RSA2048);
				encryptionKey0.setBuffer(b);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
				verificationKey.setBuffer(b);
				final PublicKeys publicKeys0=new PublicKeys();
				publicKeys0.setEncryption(TypeConverter.convert(encryptionKey0));
				publicKeys0.setVerification(verificationKey);
				client0.setKeys(publicKeys0);
				clients.put(TypeConverter.convert(0),client0);
				expected.put(new ClientId(newUser.getId(),0),encryptionKey0);
				final Client client1=new Client();
				clients.put(TypeConverter.convert(1),client1);
				client1.setComparisonIdentities(comparisonIdentities);
				client1.setUserAgent(userAgent);
				final EncryptionKey encryptionKey1=new EncryptionKey();
				encryptionKey1.setAlgorithm(EncryptionAlgorithm.RSA2048);
				final ByteBuffer c=ByteBuffer.wrap("2".getBytes());
				c.mark();
				encryptionKey1.setBuffer(c);
				final PublicKeys publicKeys1=new PublicKeys();
				publicKeys1.setEncryption(TypeConverter.convert(encryptionKey1));
				publicKeys1.setVerification(verificationKey);
				client1.setKeys(publicKeys1);
				expected.put(new ClientId(newUser.getId(),1),encryptionKey1);
				newUser.setClients(clients);
				dataStore.put(i,newUser);
				final Future<Map<ClientId,EncryptionKey>> result=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity1(i,signature,result);
				assertTrue(result.succeeded());
				final Map<ClientId,EncryptionKey> reply=result.result();
				for(final EncryptionKey e : reply.values())
				{
					e.bufferForBuffer().rewind();
				}
				assertEquals(expected,reply);
			}
		}.run();
	}

	/**
	 * Create a user (0), add a client (0) with an encryption key with invalid algorithm and expect a failure
	 */
	@Test
	public void testBroadcastNewContactIdentity1Error() throws Exception
	{
		final Long i=(long)10;
		new Base1(i)
		{
			void run() throws NoSuchAlgorithmException
			{
				final User newUser=new User();
				newUser.setId(i);
				final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
				b.mark();
				newUser.setBlind(b);
				newUser.setIdentities(new ArrayList<HashBuffer>());
				newUser.setSentRequests(new ArrayList<HashBuffer>());
				final Map<CharSequence,Client> clients=new HashMap<>(1);
				final Client client0=new Client();
				client0.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
				client0.setUserAgent(userAgent);
				final com.kareebo.contacts.server.gora.EncryptionKey encryptionKey0=new com.kareebo.contacts.server.gora.EncryptionKey();
				encryptionKey0.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.Fake);
				encryptionKey0.setBuffer(b);
				final VerificationKey verificationKey=new VerificationKey();
				verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
				verificationKey.setBuffer(b);
				final PublicKeys publicKeys0=new PublicKeys();
				publicKeys0.setEncryption(encryptionKey0);
				publicKeys0.setVerification(verificationKey);
				client0.setKeys(publicKeys0);
				clients.put(TypeConverter.convert(0),client0);
				newUser.setClients(clients);
				dataStore.put(i,newUser);
				final Future<Map<ClientId,EncryptionKey>> result=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity1(i,signature,result);
				assertTrue(result.failed());
				//noinspection ThrowableResultOfMethodCallIgnored
				assertEquals(FailedOperation.class,result.cause().getClass());
			}
		}.run();
	}

	/**
	 * Base class for all bases
	 */
	abstract private class Base extends SignatureVerifierTestBase
	{
		@Override
		SignatureVerifier construct(final DataStore<Long,User> dataStore)
		{
			return new BroadcastNewContactIdentity(dataStore);
		}
	}

	/**
	 * Base class for all tests for call 1
	 */
	private class Base1 extends Base
	{
		final private Long i;

		Base1(final Long i) throws Exception
		{
			this.i=i;
			setUp();
		}

		@Override
		PlaintextSerializer constructPlaintext()
		{
			return new LongPlaintextSerializer(i);
		}
	}
}