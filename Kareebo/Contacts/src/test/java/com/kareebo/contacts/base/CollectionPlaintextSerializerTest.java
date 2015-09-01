package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link CollectionPlaintextSerializer}
 */
public class CollectionPlaintextSerializerTest
{
	@Test
	public void testSerializeHashBuffer() throws Exception
	{
		final Set<HashBuffer> set=new HashSet<>(2);
		final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
		b.mark();
		set.add(new HashBuffer(b,HashAlgorithm.Fake));
		set.add(new HashBuffer(b,HashAlgorithm.SHA256));
		final Vector<byte[]> expected=new Vector<>(HashBufferPlaintextSerializer.LENGTH*set.size());
		for(final HashBuffer h : set)
		{
			expected.addAll(new HashBufferPlaintextSerializer(h).serialize());
		}
		check(set,expected);
	}

	private void check(final Collection<?> collection,final Vector<byte[]> expected) throws FailedOperation
	{
		final int expectedSize=expected.size();
		final Vector<byte[]> result=new CollectionPlaintextSerializer<>(collection).serialize();
		assertEquals(expectedSize,result.size());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),result.elementAt(i));
		}
	}

	@Test
	public void testSerializeContactOperation() throws Exception
	{
		final Set<ContactOperation> set=new HashSet<>(1);
		final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
		b.mark();
		set.add(new ContactOperation(new HashBuffer(b,HashAlgorithm.SHA256),ContactOperationType.Add,new
			                                                                                             EncryptedBuffer(b,EncryptionAlgorithm.RSA2048,new ClientId(0,0))));
		final Vector<byte[]> expected=new Vector<>(ContactOperationPlaintextSerializer.LENGTH*set.size());
		for(final ContactOperation c : set)
		{
			expected.addAll(new ContactOperationPlaintextSerializer(c).serialize());
		}
		check(set,expected);
	}

	@Test
	public void testSerializeEncryptedBufferPair() throws Exception
	{
		final Set<EncryptedBufferPair> set=new HashSet<>(1);
		final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
		b.mark();
		final EncryptedBuffer encryptedBuffer=new EncryptedBuffer(b,EncryptionAlgorithm.RSA2048,new ClientId(0,0));
		set.add(new EncryptedBufferPair(encryptedBuffer,encryptedBuffer));
		final Vector<byte[]> expected=new Vector<>(EncryptedBufferPairPlaintextSerializer.LENGTH*set.size());
		for(final EncryptedBufferPair e : set)
		{
			expected.addAll(new EncryptedBufferPairPlaintextSerializer(e).serialize());
		}
		check(set,expected);
	}

	@Test(expected=FailedOperation.class)
	public void testSerializeUnknownClass() throws Exception
	{
		final Set<String> set=new HashSet<>(1);
		set.add("1");
		new CollectionPlaintextSerializer<>(set).serialize();
	}
}