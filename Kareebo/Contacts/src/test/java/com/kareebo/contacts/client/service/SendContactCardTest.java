package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TException;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.*;

/**
 * Unit test for {@link SendContactCard}
 */
public class SendContactCardTest
{
	@Test
	public void testSendContactCard() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(4);
		tests.add(new SimpleTestHarness.HashBufferTestBase<Void>("u")
		{
			@Override
			protected void perform(final HashBuffer object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final
			ClientId clientId,final AsyncResultHandler<Void> handler) throws NoSuchProviderException, TException,
				                                                                 NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new SendContactCard(clientManager,signingKey,clientId).sendContactCard1(object,handler);
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptionKeys>("id")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<EncryptionKeys> clientManager,final SigningKey signingKey,final
			ClientId clientId,final AsyncResultHandler<EncryptionKeys> handler) throws NoSuchProviderException, TException,
				                                                                           NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new SendContactCard(clientManager,signingKey,clientId).sendContactCard2(object,handler);
			}
		});
		tests.add(new SimpleTestHarness.CollectionSimpleTestBase<EncryptedBufferSigned,Void>("encryptedBuffers","encryptedBuffer")
		{
			@Override
			void perform(final MyClientManager<Collection<EncryptedBufferSigned>,Void> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<Void> handler) throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				final Set<EncryptedBuffer> set=new HashSet<>(2);
				final ByteBuffer b1=ByteBuffer.wrap("a".getBytes());
				b1.mark();
				set.add(new EncryptedBuffer(b1,EncryptionAlgorithm.Fake,clientId));
				final ByteBuffer b2=ByteBuffer.wrap("b".getBytes());
				b2.mark();
				set.add(new EncryptedBuffer(b2,EncryptionAlgorithm.Fake,clientId));
				new SendContactCard(clientManager,signingKey,clientId).sendContactCard3(set,handler);
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<EncryptedBufferSignedWithVerificationKey> clientManager,final SigningKey signingKey,final
			ClientId clientId,final AsyncResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws NoSuchProviderException, TException,
				                                                                                                     NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new SendContactCard(clientManager,signingKey,clientId).sendContactCard4(object,handler);
			}
		});
		new SimpleTestHarness().test(tests);
	}
}