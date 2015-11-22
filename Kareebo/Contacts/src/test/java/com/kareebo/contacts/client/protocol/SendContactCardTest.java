package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit test for {@link SendContactCard}
 */
public class SendContactCardTest
{
	@Test
	public void testSendContactCard() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(5);
		tests.add(new SimpleTestHarness.HashBufferTestBase<Void>("u")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return com.kareebo.contacts.base.service.SendContactCard.method0;
			}			@Override
			protected ServiceMethod nextServiceMethod()
			{
				return null;
			}


		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptionKeys>("id")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return com.kareebo.contacts.base.service.SendContactCard.method1;
			}
		});
		tests.add(new SimpleTestHarness.CollectionSimpleTestBase<EncryptedBufferSigned,Void>("encryptedBuffers","encryptedBuffer")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return com.kareebo.contacts.base.service.SendContactCard.method2;
			}

			@Override
			protected ServiceMethod nextServiceMethod()
			{
				return null;
			}

			@Override
			SetEncryptedBuffer constructPayload()
			{
				final Set<EncryptedBuffer> set=new HashSet<>(2);
				final ByteBuffer b1=ByteBuffer.wrap("a".getBytes());
				b1.mark();
				set.add(new EncryptedBuffer(b1,EncryptionAlgorithm.Fake,clientId));
				final ByteBuffer b2=ByteBuffer.wrap("b".getBytes());
				b2.mark();
				set.add(new EncryptedBuffer(b2,EncryptionAlgorithm.Fake,clientId));
				return new SetEncryptedBuffer(set);
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return com.kareebo.contacts.base.service.SendContactCard.method3;
			}
		});
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return new ServiceMethod(com.kareebo.contacts.base.service.SendContactCard.method3.getServiceName(),"random");
			}

			@Override
			protected boolean serviceNotFound()
			{
				return true;
			}
		});
		new SimpleTestHarness().test(tests);
	}
}