package com.kareebo.contacts.dataStructures.common;

import org.junit.Test;

/**
 * Unit test for {@link com.kareebo.contacts.dataStructures.common.HandleConstructor}
 */
public class HandleConstructorTest
{
	private final static String buffer="A";
	private final static HandleType type=HandleType.EmailAddress;
	HandleConstructor handleConstructor=new HandleConstructor(buffer,type);

	@Test
	public void testGetBuffer() throws Exception
	{
		org.junit.Assert.assertEquals(buffer,handleConstructor.getBuffer());
	}

	@Test
	public void testGetType() throws Exception
	{
		org.junit.Assert.assertEquals(type,handleConstructor.getType());
	}
}