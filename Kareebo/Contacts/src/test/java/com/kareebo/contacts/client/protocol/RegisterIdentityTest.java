package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Unit test for {@link RegisterIdentity}
 */
public class RegisterIdentityTest
{
	@Test
	public void test() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(4);
		tests.add(new SimpleTestHarness.HashBufferTestBase<RegisterIdentityReply>("uA")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return RegisterIdentity.method1;
			}
		});
		tests.add(new SimpleTestHarness.TestBase<Long,RegisterIdentityReply>("userIdA")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return RegisterIdentity.method2;
			}

			@Override
			MyClientManager<Long,RegisterIdentityReply> clientManager(final boolean success)
			{
				return new MyClientManager<Long,RegisterIdentityReply>(success)
				{
					@Override
					void handlePayload(final Long payload) throws NoSuchFieldException, NoSuchAlgorithmException, TException,
						                                              NoSuchProviderException, InvalidKeyException, SignatureException, IllegalAccessException
					{
					}
				};
			}

			@Override
			TBase constructPayload()
			{
				return new LongId(8);
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<RegisterIdentityInput,Void>("registerIdentityInput")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return RegisterIdentity.method3;
			}

			@Override
			protected boolean isFinal()
			{
				return true;
			}

			@Override
			protected RegisterIdentityInput constructPayload()
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				return new RegisterIdentityInput(new PublicKeys(new EncryptionKey(buffer,EncryptionAlgorithm.RSA2048),new
					                                                                                                      VerificationKey(buffer,SignatureAlgorithm.Fake)),new HashBuffer(buffer,HashAlgorithm.SHA256),9,new HashSet<HashBuffer>(),new HashBuffer(buffer,HashAlgorithm.Fake),new UserAgent("a","b"),10);
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<RegisterIdentityInput,Void>("registerIdentityInput")
		{
			@Override
			protected boolean serviceNotFound()
			{
				return true;
			}

			@Override
			protected ServiceMethod getServiceMethod()
			{
				return new ServiceMethod(RegisterIdentity.serviceName,"random");
			}

			@Override
			protected RegisterIdentityInput constructPayload()
			{
				return null;
			}
		});
		new SimpleTestHarness().test(tests);
	}
}