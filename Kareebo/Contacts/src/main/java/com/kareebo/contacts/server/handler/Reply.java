package com.kareebo.contacts.server.handler;

import org.vertx.java.core.Future;

/**
 * Wrapper around a {@link org.vertx.java.core.Future} and a Thrift reply
 */
class Reply<T>
{
	private final Future future;
	private final T reply;

	Reply(final Future future)
	{
		this(future,null);
	}

	Reply(final Future future,final T reply)
	{
		this.future=future;
		this.reply=reply;
	}

	void setReply()
	{
		future.setResult(reply);
	}

	void setFailure(final Throwable throwable)
	{
		future.setFailure(throwable);
	}

	boolean failed()
	{
		return future.failed();
	}

	T result()
	{
		return (T)future.result();
	}

	Throwable cause()
	{
		return future.cause();
	}

	boolean succeeded()
	{
		return future.succeeded();
	}
}
