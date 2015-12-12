package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for {@link ModifyKeys}
 */
public class ModifyKeysTest
{
	@Test
	public void test() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(2);
		tests.add(new SimpleTestHarness.SimpleTestBase<PublicKeys,Void>("newPublicKeys")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return com.kareebo.contacts.client.protocol.ModifyKeys.method1;
			}

			@Override
			protected boolean isFinal()
			{
				return true;
			}

			@Override
			TBase constructPayload()
			{
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				return new PublicKeys(new EncryptionKey(buffer,EncryptionAlgorithm.RSA2048),new VerificationKey(buffer,SignatureAlgorithm.Fake));
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<PublicKeys,Void>("newPublicKeys")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return new ServiceMethod(com.kareebo.contacts.client.protocol.ModifyKeys
					                         .serviceName,"random");
			}

			@Override
			TBase constructPayload()
			{
				return null;
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