package com.kareebo.contacts.client.protocol;

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
	final boolean succeed;
	AsyncResultHandler<T> resultHandler;
	Class cls;

	MockClientManager(final boolean succeed)
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

	private void setResultHandler(final TAsyncMethodCall method)
	{
		final Class superClass=cls.getSuperclass();
		final Field resultHandlerField;
		try
		{
			resultHandlerField=superClass.getDeclaredField("resultHandler");
			resultHandlerField.setAccessible(true);
			//noinspection unchecked
			resultHandler=(AsyncResultHandler<T>)resultHandlerField.get(method);
			resultHandlerField.setAccessible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	abstract protected void callInternal(final TAsyncMethodCall method);
}
