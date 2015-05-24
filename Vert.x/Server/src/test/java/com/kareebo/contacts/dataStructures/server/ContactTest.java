package com.kareebo.contacts.dataStructures.server;

import com.kareebo.contacts.dataStructures.common.Id;
import org.junit.Test;

/**
 * Unit test for {@link com.kareebo.contacts.dataStructures.server.Contact}
 */
public class ContactTest
{
	final static private Id id=new Id(1);
	final static private byte[] hash=new byte[2];
	final static private String algorithm="a";
	final private Contact contact=new Contact(id,hash,algorithm);

	@Test
	public void testGetId() throws Exception
	{
		org.junit.Assert.assertEquals(id,contact.getId());
	}

	@Test
	public void testGetHash() throws Exception
	{
		org.junit.Assert.assertEquals(hash,contact.getHash());
	}

	@Test
	public void testGetAlgorithm() throws Exception
	{
		org.junit.Assert.assertEquals(algorithm,contact.getAlgorithm());
	}
}