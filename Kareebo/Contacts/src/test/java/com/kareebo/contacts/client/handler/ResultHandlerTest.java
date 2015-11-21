package com.kareebo.contacts.client.handler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ResultHandler}
 */
public class ResultHandlerTest
{
	@Test
	public void testHandleError() throws Exception
	{
		final TestErrorNotifier notifier=new TestErrorNotifier();
		final String service="a";
		final Exception cause=new Exception();
		new ResultHandler<Void>(service,notifier)
		{
			@Override
			public void handle(final Void event)
			{
			}
		}.handleError(cause);
		assertEquals(service,notifier.service);
		assertEquals(cause,notifier.cause);
	}
}