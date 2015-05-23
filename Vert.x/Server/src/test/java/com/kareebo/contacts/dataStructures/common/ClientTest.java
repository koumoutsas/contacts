package com.kareebo.contacts.dataStructures.common;

import org.junit.Test;

/**
 * Unit tests for {@link com.kareebo.contacts.dataStructures.common.Client}
 */
public class ClientTest
{
	@Test
	public void testGetId() throws Exception
	{
		final Id id=new Id("1");
		final Client client=new Client("a","b",id);
		org.junit.Assert.assertEquals(id,client.getId());
	}
}