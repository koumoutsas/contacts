package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.ResultHandler;
import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testSendContactCard() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(5);
		tests.add(new SimpleTestHarness.HashBufferTestBase<Void>("u")
		{
			@Override
			protected void perform(final HashBuffer object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final
			ClientId clientId,final ResultHandler<Void> handler) throws NoSuchProviderException, TException,
				                                                            NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new SendContactCard(clientManager,signingKey,clientId).sendContactCard1(object,handler);
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptionKeys>("id")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<EncryptionKeys> clientManager,final SigningKey signingKey,final
			ClientId clientId,final ResultHandler<EncryptionKeys> handler) throws NoSuchProviderException, TException,
				                                                                      NoSuchAlgorithmException, InvalidKeyException, SignatureException, ServiceFactory.NoSuchService, ServiceFactory.NoSuchMethod
			{
				ServiceFactory.run(clientManager,signingKey,clientId,com.kareebo.contacts.base.service.SendContactCard.method1,object.getId(),new ResultConnectorReverse<>
					                                                                                                                              (handler));
			}
		});
		tests.add(new SimpleTestHarness.CollectionSimpleTestBase<EncryptedBufferSigned,Void>("encryptedBuffers","encryptedBuffer")
		{
			@Override
			void perform(final MyClientManager<Collection<EncryptedBufferSigned>,Void> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<Void> handler) throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
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
			ClientId clientId,final ResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws NoSuchProviderException, TException,
				                                                                                                NoSuchAlgorithmException, InvalidKeyException, SignatureException, ServiceFactory.NoSuchService, ServiceFactory.NoSuchMethod
			{
				ServiceFactory.run(clientManager,signingKey,clientId,com.kareebo.contacts.base.service.SendContactCard.method3,object.getId(),new ResultConnectorReverse<>
					                                                                                                                              (handler));
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<EncryptedBufferSignedWithVerificationKey> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, Service.NoSuchMethod
			{
				thrown.expect(Service.NoSuchMethod.class);
				new SendContactCard(clientManager,signingKey,clientId).run(new NotificationMethod(com.kareebo.contacts
					                                                                                  .base
					                                                                                  .service.SendContactCard
					                                                                                  .method3.getServiceName(),"random"),object.getId(),new ResultConnectorReverse<>(handler));
			}
		});
		new SimpleTestHarness().test(tests);
	}
}