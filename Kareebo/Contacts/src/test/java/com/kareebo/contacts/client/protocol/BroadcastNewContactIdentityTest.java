package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Unit test for {@link BroadcastNewContactIdentity}
 */
public class BroadcastNewContactIdentityTest
{
	@Test
	public void test() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(6);
		tests.add(new SimpleTestHarness.LongIdTestBase<Map<ClientId,EncryptionKey>>("userIdB")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return BroadcastNewContactIdentity.method1;
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<EncryptedBufferPairSet,Map<ClientId,EncryptionKey>>("encryptedBufferPairs")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return BroadcastNewContactIdentity.method2;
			}

			@Override
			protected EncryptedBufferPairSet constructPayload()
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
		tests.add(new EncryptedBufferSignedTestBase(BroadcastNewContactIdentity.method3));
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return BroadcastNewContactIdentity.method4;
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<HashBufferPair,Void>("uCs")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return BroadcastNewContactIdentity.method5;
			}

			@Override
			protected HashBufferPair constructPayload()
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				return new HashBufferPair(new HashBuffer(buffer,HashAlgorithm.Fake),new HashBuffer(buffer,HashAlgorithm.SHA256));
			}

			@Override
			protected boolean isFinal()
			{
				return true;
			}
		});
		new SimpleTestHarness().test(tests);
	}
}