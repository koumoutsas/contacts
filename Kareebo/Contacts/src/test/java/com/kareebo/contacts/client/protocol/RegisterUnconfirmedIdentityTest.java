package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.HashBufferSet;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit test for {@link RegisterUnconfirmedIdentity}
 */
public class RegisterUnconfirmedIdentityTest
{
	@Test
	public void test() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(2);
		tests.add(new SimpleTestHarness.SimpleTestBase<HashBufferSet,Void>("uSet")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return RegisterUnconfirmedIdentity.method1;
			}

			@Override
			protected HashBufferSet constructPayload()
			{
				final Set<HashBuffer> hashBuffers=new HashSet<>(2);
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				hashBuffers.add(new HashBuffer(buffer,HashAlgorithm.Fake));
				hashBuffers.add(new HashBuffer(buffer,HashAlgorithm.SHA256));
				return new HashBufferSet(hashBuffers);
			}

			@Override
			protected boolean isFinal()
			{
				return true;
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<HashBufferSet,Void>("uSet")
		{
			@Override
			protected boolean serviceNotFound()
			{
				return true;
			}

			@Override
			protected ServiceMethod getServiceMethod()
			{
				return new ServiceMethod(RegisterUnconfirmedIdentity
					                         .serviceName,"random");
			}			@Override
			TBase constructPayload()
			{
				return null;
			}


		});
		new SimpleTestHarness().test(tests);
	}
}