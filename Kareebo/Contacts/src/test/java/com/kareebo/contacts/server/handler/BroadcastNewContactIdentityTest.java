package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.LongPlaintextSerializer;
import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;
import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.gora.store.DataStore;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link BroadcastNewContactIdentity}
 */
public class BroadcastNewContactIdentityTest
{
	@Test
	public void testBroadcastNewContactIdentity1() throws Exception
	{
		final Long i=(long)10;
		new Base1(i)
		{
			void run() throws NoSuchAlgorithmException
			{
				final Map<Long,EncryptionKey> expected=new HashMap<>(2);
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
				expected.put((long)0,encryptionKey0);
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
				expected.put((long)1,encryptionKey1);
				newUser.setClients(clients);
				dataStore.put(i,newUser);
				final Future<Map<Long,EncryptionKey>> result=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity1(i,signature,result);
				assertTrue(result.succeeded());
				final Map<Long,EncryptionKey> reply=result.result();
				for(final EncryptionKey e : reply.values())
				{
					e.bufferForBuffer().rewind();
				}
				assertEquals(expected,reply);
			}
		}.run();
	}

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
				final Future<Map<Long,EncryptionKey>> result=new DefaultFutureResult<>();
				((BroadcastNewContactIdentity)signatureVerifier).broadcastNewContactIdentity1(i,signature,result);
				assertTrue(result.failed());
				//noinspection ThrowableResultOfMethodCallIgnored
				assertEquals(FailedOperation.class,result.cause().getClass());
			}
		}.run();
	}

	private class Base1 extends SignatureVerifierTestBase
	{
		final private Long i;

		Base1(final Long i) throws Exception
		{
			this.i=i;
			setUp();
		}

		@Override
		SignatureVerifier construct(final DataStore<Long,User> dataStore)
		{
			return new BroadcastNewContactIdentity(dataStore);
		}

		@Override
		PlaintextSerializer constructPlaintext()
		{
			return new LongPlaintextSerializer(i);
		}
	}
}