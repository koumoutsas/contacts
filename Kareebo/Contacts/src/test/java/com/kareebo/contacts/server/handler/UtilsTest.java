package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashAlgorithm;
import com.kareebo.contacts.server.gora.HashBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Utils}
 */
public class UtilsTest
{
	static
	{
		// For 100% coverage
		new Utils();
	}

	@Test
	public void testConvertToSet() throws Exception
	{
		final List<HashBuffer> list=new ArrayList<>(2);
		final ByteBuffer b1=ByteBuffer.wrap("1".getBytes());
		b1.mark();
		final HashBuffer h1=new HashBuffer();
		h1.setBuffer(b1);
		h1.setAlgorithm(HashAlgorithm.Fake);
		list.add(h1);
		final ByteBuffer b2=ByteBuffer.wrap("2".getBytes());
		b2.mark();
		final HashBuffer h2=new HashBuffer();
		h2.setBuffer(b2);
		h2.setAlgorithm(HashAlgorithm.SHA256);
		list.add(h2);
		list.add(h2);
		final Set<HashBuffer> set=Utils.convertToSet(list);
		assertEquals(list.size()-1,set.size());
		list.forEach(l->assertTrue(set.contains(l)));
	}
}