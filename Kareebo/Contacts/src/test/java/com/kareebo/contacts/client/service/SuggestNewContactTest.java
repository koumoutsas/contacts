package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.*;

/**
 * Unit test for {@link SuggestNewContactTest}
 */
public class SuggestNewContactTest
{
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void test() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, TException, InvalidKeyException, SignatureException, ServiceFactory.NoSuchService, ServiceFactory.NoSuchMethod, NotifiableService.NoSuchMethod
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(5);
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptionKeysWithHashBuffer>("id")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<EncryptionKeysWithHashBuffer> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<EncryptionKeysWithHashBuffer> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ServiceFactory.NoSuchService, ServiceFactory.NoSuchMethod
			{
				ServiceFactory.run(clientManager,signingKey,clientId,com.kareebo.contacts.base.service.SuggestNewContact.method0,object.getId(),new ResultConnectorReverse<>(handler));
			}
		});
		tests.add(new SimpleTestHarness.HashBufferTestBase<Void>("uB")
		{
			@Override
			protected void perform(final HashBuffer object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new SuggestNewContact(clientManager,signingKey,clientId).suggestNewContact2(new HashSet<EncryptedBuffer>(),object,handler);
			}
		});
		tests.add(new SimpleTestHarness.CollectionSimpleTestBase<EncryptedBuffer,Void>("encryptedBuffers","encryptedBuffer")
		{
			@Override
			void perform(final MyClientManager<Collection<EncryptedBuffer>,Void> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<Void> handler) throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final Set<EncryptedBuffer> encryptedBuffers=new HashSet<>(2);
				encryptedBuffers.add(new EncryptedBuffer(buffer,EncryptionAlgorithm.Fake,clientId));
				encryptedBuffers.add(new EncryptedBuffer(buffer,EncryptionAlgorithm.RSA2048,clientId));
				new SuggestNewContact(clientManager,signingKey,clientId).suggestNewContact2(encryptedBuffers,new HashBuffer(buffer,
					                                                                                                           HashAlgorithm.Fake),handler);
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<EncryptedBufferSignedWithVerificationKey> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ServiceFactory.NoSuchService, ServiceFactory.NoSuchMethod
			{
				ServiceFactory.run(clientManager,signingKey,clientId,com.kareebo.contacts.base.service.SuggestNewContact.method2,object.getId(),
					new ResultConnectorReverse<>
						(handler));
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected void perform(final LongId object,final MockClientManager<EncryptedBufferSignedWithVerificationKey> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<EncryptedBufferSignedWithVerificationKey> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NotifiableService.NoSuchMethod
			{
				thrown.expect(NotifiableService.NoSuchMethod.class);
				new SuggestNewContact(clientManager,signingKey,clientId).run(new NotificationMethod(com.kareebo.contacts
					                                                                                    .base
					                                                                                    .service.SuggestNewContact
					                                                                                    .method2.getServiceName(),"random"),object.getId(),new ResultConnectorReverse<>(handler));
			}
		});
		new SimpleTestHarness().test(tests);
	}
}