package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.*;
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
		final HashAlgorithm algorithm=HashAlgorithm.SHA256;
		final ByteBuffer byteBuffer=ByteBuffer.wrap("abc".getBytes());
		byteBuffer.mark();
		final HashBuffer hashBuffer=new HashBuffer(byteBuffer,algorithm);
		final Set<ContactOperation> contactOperations=new HashSet<>(2);
		final EncryptedBuffer encryptedBuffer=new EncryptedBuffer(byteBuffer,EncryptionAlgorithm.RSA2048,new ClientId(0,0));
		final ByteBuffer emptyByteBuffer=ByteBuffer.wrap("".getBytes());
		emptyByteBuffer.mark();
		contactOperations.add(new ContactOperation(new HashBuffer(emptyByteBuffer,HashAlgorithm.Fake),ContactOperationType
			                                                                                              .Add,encryptedBuffer));
		contactOperations.add(new ContactOperation(hashBuffer,
			                                          ContactOperationType.Delete,encryptedBuffer));
		contactOperations.add(new ContactOperation(hashBuffer,
			                                          ContactOperationType.Update,new EncryptedBuffer
				                                                                      (emptyByteBuffer,EncryptionAlgorithm.Fake,new ClientId(-1,-1))));
		final Vector<byte[]> plaintext=new ContactOperationSetPlaintextSerializer(contactOperations)
			                               .serialize();
		assertEquals(contactOperations.size()*ContactOperationPlaintextSerializer.LENGTH,plaintext.size());
		final Vector<byte[]> expected=new Vector<>();
		for(final ContactOperation contactOperation : contactOperations)
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