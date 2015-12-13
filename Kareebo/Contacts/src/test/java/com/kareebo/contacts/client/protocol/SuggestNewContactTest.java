package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit test for {@link SuggestNewContact}
 */
public class SuggestNewContactTest
{
	@Test
	public void test() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(5);
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptionKeysWithHashBuffer>("id")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return SuggestNewContact.method1;
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<EncryptedBuffersWithHashBuffer,Void>("uB")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return SuggestNewContact.method2;
			}

			@Override
			protected ServiceMethod nextServiceMethod()
			{
				return null;
			}

			@Override
			EncryptedBuffersWithHashBuffer constructPayload()
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final Set<EncryptedBuffer> encryptedBuffers=new HashSet<>(2);
				encryptedBuffers.add(new EncryptedBuffer(buffer,EncryptionAlgorithm.Fake,clientId));
				encryptedBuffers.add(new EncryptedBuffer(buffer,EncryptionAlgorithm.RSA2048,clientId));
				return new EncryptedBuffersWithHashBuffer(encryptedBuffers,new HashBuffer(buffer,
					                                                                         HashAlgorithm.Fake));
			}
		});
		tests.add(new SimpleTestHarness.CollectionSimpleTestBase<EncryptedBuffer,Void>("encryptedBuffers","encryptedBuffer")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return SuggestNewContact.method2;
			}

			@Override
			protected ServiceMethod nextServiceMethod()
			{
				return null;
			}

			@Override
			EncryptedBuffersWithHashBuffer constructPayload()
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final Set<EncryptedBuffer> encryptedBuffers=new HashSet<>(2);
				encryptedBuffers.add(new EncryptedBuffer(buffer,EncryptionAlgorithm.Fake,clientId));
				encryptedBuffers.add(new EncryptedBuffer(buffer,EncryptionAlgorithm.RSA2048,clientId));
				return new EncryptedBuffersWithHashBuffer(encryptedBuffers,new HashBuffer(buffer,
					                                                                         HashAlgorithm.Fake));
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return SuggestNewContact.method3;
			}
		});
		new SimpleTestHarness().test(tests);
	}
}