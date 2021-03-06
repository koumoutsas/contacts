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
 * Unit test for {@link UpdateServerContactBook}
 */
public class UpdateServerContactBookTest
{
	@Test
	public void test() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(2);
		tests.add(new SimpleTestHarness.SimpleTestBase<ContactOperationSet,Void>("contactOperationSet")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return UpdateServerContactBook.method1;
			}

			@Override
			protected ContactOperationSet constructPayload()
			{
				final ClientId clientId=new ClientId(9,9);
				final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
				buffer.mark();
				final Set<ContactOperation> contactOperations=new HashSet<>(2);
				contactOperations.add(new ContactOperation(new HashBuffer(buffer,HashAlgorithm.SHA256),ContactOperationType.Add,new EncryptedBuffer
					                                                                                                                (buffer,
						                                                                                                                EncryptionAlgorithm.RSA2048,
						                                                                                                                clientId)));
				contactOperations.add(new ContactOperation(new HashBuffer(buffer,HashAlgorithm.SHA256),ContactOperationType.Delete,new
					                                                                                                                   EncryptedBuffer(buffer,EncryptionAlgorithm.RSA2048,
						                                                                                                                                  clientId)));
				return new ContactOperationSet(contactOperations);
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