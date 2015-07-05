package com.kareebo.contacts.base;

import com.google.common.primitives.Longs;
import com.kareebo.contacts.common.Algorithm;
import com.kareebo.contacts.common.CryptoBuffer;
import com.kareebo.contacts.common.HashedContact;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link HashedContactPlaintextSerializer}
 */
public class HashedContactPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final long id=536;
		final Algorithm algorithm=Algorithm.SHA256;
		final byte[] bytes="abc".getBytes();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
		byteBuffer.mark();
		final CryptoBuffer cryptoBuffer=new CryptoBuffer(byteBuffer,algorithm);
		final Vector<byte[]> plaintext=new HashedContactPlaintextSerializer(new HashedContact(id,
			                                                                                     cryptoBuffer)).serialize();
		assertEquals(CryptoBufferPlaintextSerializer.LENGTH+1,plaintext.size());
		assertEquals(id,Longs.fromByteArray(plaintext.elementAt(0)));
		plaintext.remove(0);
		final Vector<byte[]> expected=new CryptoBufferPlaintextSerializer(cryptoBuffer).serialize();
		assertEquals(expected.size(),plaintext.size());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),plaintext.elementAt(i));
		}
	}
}