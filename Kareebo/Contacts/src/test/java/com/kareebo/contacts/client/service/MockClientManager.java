package com.kareebo.contacts.client.service;

import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.async.TAsyncMethodCall;
import org.apache.thrift.transport.TClientTransport;
import org.apache.thrift.transport.THttpClientTransport;
import org.apache.thrift.transport.TTransportException;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.VertxFactory;

import java.lang.reflect.Field;

import static org.junit.Assert.fail;

/**
 * Mock {@link org.apache.thrift.async.TAsyncClientManager}
 */
abstract class MockClientManager<T> extends TAsyncClientManager
{
	protected final boolean succeed;
	protected AsyncResultHandler<T> resultHandler;
	protected Class cls;

	public MockClientManager(final boolean succeed)
	{
		super(new TClientTransport(new THttpClientTransport.Args(VertxFactory.newVertx(),0))
		{
			@Override
			public boolean isOpen()
			{
				return true;
			}

			@Override
			public void open() throws TTransportException
			{
			}

			@Override
			public void close()
			{
			}

			@Override
			public void write(final byte[] bytes,final int i,final int i1) throws TTransportException
			{
			}
		},null);
		this.succeed=succeed;
	}

	@Override
	public void call(TAsyncMethodCall method)
	{
		cls=method.getClass();
		setResultHandler(method);
		callInternal(method);
	}

	@SuppressWarnings("unchecked")
	private void setResultHandler(final TAsyncMethodCall method)
	{
		final Class superClass=cls.getSuperclass();
		final Field resultHandlerField;
		try
		{
			resultHandlerField=superClass.getDeclaredField("resultHandler");
			resultHandlerField.setAccessible(true);
			resultHandler=(AsyncResultHandler<T>)resultHandlerField.get(method);
			resultHandlerField.setAccessible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	abstract void callInternal(final TAsyncMethodCall method);
}
