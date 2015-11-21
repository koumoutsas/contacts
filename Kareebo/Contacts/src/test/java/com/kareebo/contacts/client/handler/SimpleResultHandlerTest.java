package com.kareebo.contacts.client.handler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link SimpleResultHandler}
 */
public class SimpleResultHandlerTest
{
	@Test
	public void testHandle() throws Exception
	{
		final String service="a";
		final TestSuccessNotifier successNotifier=new TestSuccessNotifier();
		final SimpleResultHandler handler=new SimpleResultHandler(service,successNotifier,new TestErrorNotifier());
		handler.handle(null);
		assertEquals(service,successNotifier.service);
	}
}