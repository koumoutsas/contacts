package com.kareebo.contacts.client.service;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link AsyncResultHandlerTest}
 */
public class AsyncResultHandlerTest
{
	@Test
	public void testHandle() throws Exception
	{
		final MockHandler<Void> handler=new MockHandler<>();
		new AsyncResultHandler<>(handler).handle(new MockVoidResult(true));
		assertTrue(handler.succeeded);
		new AsyncResultHandler<>(handler).handle(new MockVoidResult(false));
		assertFalse(handler.succeeded);
	}

	private class MockVoidResult extends MockResult<Void>
	{
		/**
		 * @param success Whether the result was successful
		 */
		MockVoidResult(final boolean success)
		{
			super(success);
		}

		@Override
		public Void result()
		{
			return null;
		}
	}
}