package com.kareebo.contacts.base;

import com.kareebo.contacts.common.Algorithm;
import com.kareebo.contacts.common.CryptoBuffer;
import com.kareebo.contacts.common.PublicKeys;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link PublicKeysPlaintextSerializer}
 */
public class PublicKeysPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final CryptoBuffer encryption=new CryptoBuffer();
		encryption.setAlgorithm(Algorithm.SHA256);
		encryption.setBuffer("abc".getBytes());
		final CryptoBuffer verification=new CryptoBuffer();
		verification.setAlgorithm(Algorithm.SHA256);
		verification.setBuffer("abc".getBytes());
		final PublicKeys publicKeys=new PublicKeys(encryption,verification);
		final Vector<byte[]> plaintext=new PublicKeysPlaintextSerializer(publicKeys).serialize();
		assertEquals(2*CryptoBufferPlaintextSerializer.LENGTH,plaintext.size());
		final Vector<byte[]> expected=new CryptoBufferPlaintextSerializer(encryption).serialize();
		expected.addAll(new CryptoBufferPlaintextSerializer(verification).serialize());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),plaintext.elementAt(i));
		}
	}
}