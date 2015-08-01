package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.ContactOperation;
import com.kareebo.contacts.thrift.ContactOperationType;
import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ContactOperationSetPlaintextSerializer}
 */
public class ContactOperationSetPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final ContactOperationType contactOperationTypeAdd=ContactOperationType.Add;
		final ContactOperationType contactOperationTypeDelete=ContactOperationType.Delete;
		final HashAlgorithm algorithm=HashAlgorithm.SHA256;
		final byte[] bytes="abc".getBytes();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
		byteBuffer.mark();
		final HashBuffer hashBuffer=new HashBuffer(byteBuffer,algorithm);
		final Set<ContactOperation> contactOperations=new HashSet<>(2);
		contactOperations.add(new ContactOperation(hashBuffer,contactOperationTypeAdd));
		contactOperations.add(new ContactOperation(hashBuffer,
			                                          contactOperationTypeDelete));
		final Vector<byte[]> plaintext=new ContactOperationSetPlaintextSerializer(contactOperations)
			                               .serialize();
		assertEquals(2*ContactOperationPlaintextSerializer.LENGTH,plaintext.size());
		final Vector<byte[]> expected=new Vector<>();
		for(ContactOperation contactOperation : contactOperations)
		{
			expected.addAll(new ContactOperationPlaintextSerializer(contactOperation).serialize());
		}
		assertEquals(expected.size(),plaintext.size());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),plaintext.elementAt(i));
		}
	}
}