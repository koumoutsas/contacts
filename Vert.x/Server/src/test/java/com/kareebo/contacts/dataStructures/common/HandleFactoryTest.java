package com.kareebo.contacts.dataStructures.common;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * Unit test for {@link com.kareebo.contacts.dataStructures.common.HandleFactory}
 */
public class HandleFactoryTest
{
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testCreateNull() throws Exception
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Null argument");
		HandleFactory.create(null);
	}

	@Test
	public void testCreateEmailAddress() throws Exception
	{
		final EmailAddress e=new EmailAddress("a@b.c");
		final HandleConstructor hc=new HandleConstructor(e.toString(),HandleType.EmailAddress);
		final Handle h=HandleFactory.create(hc);
		org.junit.Assert.assertNotNull(h);
		org.junit.Assert.assertThat(h,is(instanceOf(EmailAddress.class)));
		org.junit.Assert.assertEquals(e,h);
	}
}