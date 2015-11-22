package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TException;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Unit test for {@link RegisterIdentity}
 */
public class RegisterIdentityTest
{
	@Test
	public void test() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, TException, InvalidKeyException, SignatureException, ServiceFactory.NoSuchService, ServiceFactory.NoSuchMethod, NotifiableService.NoSuchMethod
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(3);
		tests.add(new SimpleTestHarness.HashBufferTestBase<RegisterIdentityReply>("uA")
		{
			@Override
			protected void perform(final HashBuffer object,final MockClientManager<RegisterIdentityReply> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<RegisterIdentityReply> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new RegisterIdentity(clientManager,signingKey,clientId).registerIdentity1(object,handler);
			}
		});
		tests.add(new SimpleTestHarness.LongTestBase<RegisterIdentityReply>("userIdA")
		{
			@Override
			void perform(final MyClientManager<Long,RegisterIdentityReply> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<RegisterIdentityReply> handler) throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
			{
				new RegisterIdentity(clientManager,signingKey,clientId).registerIdentity2(8,handler);
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<RegisterIdentityInput,Void>("registerIdentityInput")
		{
			@Override
			protected void perform(final RegisterIdentityInput object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
			{
				new RegisterIdentity(clientManager,signingKey,clientId).registerIdentity3(object,handler);
			}

			@Override
			protected RegisterIdentityInput construct()
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				return new RegisterIdentityInput(new PublicKeys(new EncryptionKey(buffer,EncryptionAlgorithm.RSA2048),new
					                                                                                                      VerificationKey(buffer,SignatureAlgorithm.Fake)),new HashBuffer(buffer,HashAlgorithm.SHA256),9,new HashSet<HashBuffer>(),new HashBuffer(buffer,HashAlgorithm.Fake),new UserAgent("a","b"),10);
			}
		});
		new SimpleTestHarness().test(tests);
	}
}