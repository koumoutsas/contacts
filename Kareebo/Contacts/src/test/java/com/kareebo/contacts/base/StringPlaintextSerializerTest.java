package com.kareebo.contacts.base;

import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link StringPlaintextSerializer}
 */
public class StringPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final String s="mine";
		final Vector<byte[]> result=new StringPlaintextSerializer(s).serialize();
		assertEquals(1,result.size());
		assertEquals(s,new String(result.elementAt(0)));
	}
}