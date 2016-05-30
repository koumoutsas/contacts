package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.EncryptedBufferSignedWithVerificationKey;
import com.kareebo.contacts.thrift.EncryptionKeys;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
				return SendContactCard.method1;
			}

			@Override
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
				return SendContactCard.method2;
			}
		});
		tests.add(new EncryptedBufferSignedTestBase(SendContactCard.method3));
		tests.add(new SimpleTestHarness.LongIdTestBase<EncryptedBufferSignedWithVerificationKey>("id")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return SendContactCard.method4;
			}
		});
		new SimpleTestHarness().test(tests);
	}
}