package com.kareebo.contacts.server.handler;

import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import static org.junit.Assert.*;

/**
 * Unit test for {@link Reply}
 */
public class ReplyTest
{
	@Test
	public void testSetReply() throws Exception
	{
		final Future<String> future=new DefaultFutureResult<>();
		final String result="abc";
		final Reply<String> reply=new Reply<>(future,result);
		reply.setReply();
		assertTrue(reply.succeeded());
		assertEquals(result,reply.result());
	}

	@Test
	public void testSetFailure() throws Exception
	{
		final Future<String> future=new DefaultFutureResult<>();
		final String result="abc";
		final Reply<String> reply=new Reply<>(future,result);
		final Throwable t=new Exception();
		reply.setFailure(t);
		assertTrue(reply.failed());
		assertEquals(t,reply.cause());
	}

	@Test
	public void testSetNullReply() throws Exception
	{
		final Future<Void> future=new DefaultFutureResult<>();
		final Reply<Void> reply=new Reply<>(future);
		reply.setReply();
		assertTrue(reply.succeeded());
		assertNull(reply.result());
	}
}