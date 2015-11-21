package com.kareebo.contacts.client.handler;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.client.service.AsyncSuccessResult;
import com.kareebo.contacts.client.service.KeyPair;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.async.TAsyncMethodCall;
import org.apache.thrift.transport.TClientTransport;
import org.apache.thrift.transport.THttpClientTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Test;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.VertxFactory;

import java.lang.reflect.Field;
import java.security.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

abstract public class SimpleTestBase
{
	protected final ClientId clientId=new ClientId(0,0);
	protected final SigningKey signingKey;
	protected final TAsyncClientManager clientManager=new MockClientManager();
	protected final TestSuccessNotifier successNotifier=new TestSuccessNotifier();
	protected final ErrorNotifier errorNotifier=new TestErrorNotifier();

	public SimpleTestBase() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
		signingKey=new SigningKey(new KeyPair().getPrivate(),SignatureAlgorithm.SHA256withECDSAprime239v1);
	}

	@Test
	public void testRunImplementation() throws Exception
	{
		run();
		assertEquals(serviceName(),successNotifier.service);
	}

	abstract protected void run() throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException;

	abstract protected String serviceName();

	private static class MockClientManager extends TAsyncClientManager
	{
		private AsyncResultHandler<Void> resultHandler;

		MockClientManager()
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
		}

		@Override
		@SuppressWarnings("unchecked")
		public void call(TAsyncMethodCall method)
		{
			final Class superClass=method.getClass().getSuperclass();
			final Field resultHandlerField;
			try
			{
				resultHandlerField=superClass.getDeclaredField("resultHandler");
				resultHandlerField.setAccessible(true);
				resultHandler=(AsyncResultHandler<Void>)resultHandlerField.get(method);
				resultHandlerField.setAccessible(false);
				resultHandler.handle(new AsyncSuccessResult<Void>(null));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fail();
			}
		}
	}
}
