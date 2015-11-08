package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TException;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.*;

/**
 * Unit test for {@link BroadcastNewContactIdentity}
 */
public class BroadcastNewContactIdentityTest
{
	@Test
	public void test() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, TException, InvalidKeyException, SignatureException
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(5);
		tests.add(new SimpleTestHarness.SimpleTestBase<LongId,Map<ClientId,EncryptionKey>>("userIdB")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<Map<ClientId,EncryptionKey>> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<Map<ClientId,EncryptionKey>> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new BroadcastNewContactIdentity(clientManager,signingKey,clientId).broadcastNewContactIdentity1(object,handler);
			}

			@Override
			protected LongId construct()
			{
				return new LongId(9);
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<EncryptedBufferPairSet,Map<ClientId,EncryptionKey>>("encryptedBufferPairs")
		{
			@Override
			protected void perform(final EncryptedBufferPairSet object,final MockClientManager<Map<ClientId,EncryptionKey>> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<Map<ClientId,EncryptionKey>> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new BroadcastNewContactIdentity(clientManager,signingKey,clientId).broadcastNewContactIdentity2(object,handler);
			}

			@Override
			protected EncryptedBufferPairSet construct()
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final Set<EncryptedBufferPair> set=new HashSet<>(2);
				final ClientId clientId=new ClientId(0,0);
				set.add(new EncryptedBufferPair(new EncryptedBuffer(buffer,EncryptionAlgorithm.Fake,clientId),new EncryptedBuffer
					                                                                                              (buffer,
						                                                                                              EncryptionAlgorithm.RSA2048,clientId)));
				set.add(new EncryptedBufferPair(new EncryptedBuffer(buffer,EncryptionAlgorithm.Fake,clientId),new EncryptedBuffer
					                                                                                              (buffer,
						                                                                                              EncryptionAlgorithm.RSA2048,clientId)));
				return new EncryptedBufferPairSet(set);
			}
		});
		tests.add(new SimpleTestHarness.CollectionSimpleTestBase<EncryptedBuffer,Void>("encryptedBuffers","encryptedBuffer")
		{
			@Override
			void perform(final MyClientManager<Collection<EncryptedBuffer>,Void> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<Void> handler) throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				final Set<EncryptedBuffer> set=new HashSet<>(2);
				final ByteBuffer b1=ByteBuffer.wrap("a".getBytes());
				b1.mark();
				set.add(new EncryptedBuffer(b1,EncryptionAlgorithm.Fake,clientId));
				final ByteBuffer b2=ByteBuffer.wrap("b".getBytes());
				b2.mark();
				set.add(new EncryptedBuffer(b2,EncryptionAlgorithm.Fake,clientId));
				new BroadcastNewContactIdentity(clientManager,signingKey,clientId).broadcastNewContactIdentity3(set,handler);
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<LongId,EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<EncryptedBufferSignedWithVerificationKey> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new BroadcastNewContactIdentity(clientManager,signingKey,clientId).broadcastNewContactIdentity4(object,handler);
			}

			@Override
			protected LongId construct()
			{
				return new LongId(8);
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<HashBufferPair,Void>("uCs")
		{
			@Override
			protected void perform(final HashBufferPair object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new BroadcastNewContactIdentity(clientManager,signingKey,clientId).broadcastNewContactIdentity5(object,handler);
			}

			@Override
			protected HashBufferPair construct()
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				return new HashBufferPair(new HashBuffer(buffer,HashAlgorithm.Fake),new HashBuffer(buffer,HashAlgorithm.SHA256));
			}
		});
		new SimpleTestHarness().test(tests);
	}
}