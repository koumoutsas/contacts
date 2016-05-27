package com.kareebo.contacts.server.handler;

import org.vertx.java.core.Future;

import javax.annotation.Nonnull;

/**
 * Wrapper around a {@link org.vertx.java.core.Future} and a Thrift reply
 */
class Reply<T>
{
	private final Future<T> future;
	private final T reply;

	Reply(final @Nonnull Future<T> future)
	{
		this(future,null);
	}

	Reply(final @Nonnull Future<T> future,final T reply)
	{
		this.future=future;
		this.reply=reply;
	}

	void setReply()
	{
		future.setResult(reply);
	}

	void setFailure(final @Nonnull Throwable throwable)
	{
		future.setFailure(throwable);
	}

	boolean failed()
	{
		return future.failed();
	}

	@Nonnull
	T result()
	{
		return future.result();
	}

	@Nonnull
	Throwable cause()
	{
		return future.cause();
	}

	boolean succeeded()
	{
		return future.succeeded();
	}
}