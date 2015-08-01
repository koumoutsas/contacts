package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.ContactOperation;
import com.kareebo.contacts.thrift.ContactOperationType;
import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ContactOperationPlaintextSerializer}
 */
public class ContactOperationPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final ContactOperationType contactOperationType=ContactOperationType.Add;
		final HashAlgorithm algorithm=HashAlgorithm.SHA256;
		final byte[] bytes="abc".getBytes();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
		byteBuffer.mark();
		final HashBuffer hashBuffer=new HashBuffer(byteBuffer,algorithm);
		final Vector<byte[]> plaintext=new ContactOperationPlaintextSerializer(new ContactOperation(hashBuffer,contactOperationType)).serialize();
		assertEquals(ContactOperationPlaintextSerializer.LENGTH,plaintext.size());
		final Vector<byte[]> expected=new HashBufferPlaintextSerializer(hashBuffer).serialize();
		expected.addAll(new EnumPlaintextSerializer<>(contactOperationType).serialize());
		assertEquals(expected.size(),plaintext.size());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),plaintext.elementAt(i));
		}
	}
}