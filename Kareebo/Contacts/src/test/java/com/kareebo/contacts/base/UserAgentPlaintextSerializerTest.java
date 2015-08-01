package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.UserAgent;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link UserAgentPlaintextSerializer}
 */
public class UserAgentPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final UserAgent userAgent=new UserAgent("abc","cde");
		final Vector<byte[]> plaintext=new UserAgentPlaintextSerializer(userAgent).serialize();
		assertEquals(2,plaintext.size());
		assertArrayEquals(userAgent.getPlatform().getBytes(),plaintext.elementAt(0));
		assertArrayEquals(userAgent.getVersion().getBytes(),plaintext.elementAt(1));
	}
}