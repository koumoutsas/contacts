package com.kareebo.contacts.client.handler;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link SimpleServiceHandler}
 */
public class SimpleServiceHandlerTest
{
	@Test
	public void testRun() throws Exception
	{
		final TestSuccessNotifier successNotifier=new TestSuccessNotifier();
		final TestErrorNotifier errorNotifier=new TestErrorNotifier();
		new TestSimpleServiceHandler(successNotifier,errorNotifier,null).run();
		assertEquals(TestSimpleServiceHandler.class.getSimpleName(),successNotifier.service);
		new TestSimpleServiceHandler(successNotifier,errorNotifier,new NoSuchProviderException()).run();
		assertTrue(errorNotifier.cause instanceof NoSuchProviderException);
		new TestSimpleServiceHandler(successNotifier,errorNotifier,new TException()).run();
		assertTrue(errorNotifier.cause instanceof TException);
		new TestSimpleServiceHandler(successNotifier,errorNotifier,new NoSuchAlgorithmException()).run();
		assertTrue(errorNotifier.cause instanceof NoSuchAlgorithmException);
		new TestSimpleServiceHandler(successNotifier,errorNotifier,new InvalidKeyException()).run();
		assertTrue(errorNotifier.cause instanceof InvalidKeyException);
		new TestSimpleServiceHandler(successNotifier,errorNotifier,new SignatureException()).run();
		assertTrue(errorNotifier.cause instanceof SignatureException);
	}

	static private class TestSimpleServiceHandler extends SimpleServiceHandler
	{
		final Throwable cause;

		TestSimpleServiceHandler(final SuccessNotifier onSuccess,final ErrorNotifier onError,final Throwable cause)
		{
			super(onSuccess,onError);
			this.cause=cause;
		}

		void run()
		{
			run(null);
		}

		@Override
		protected void runImplementation(final TBase input,final ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
		{
			if(cause==null)
			{
				handler.handle(null);
			}
			else if(cause instanceof NoSuchProviderException)
			{
				throw new NoSuchProviderException();
			}
			else if(cause instanceof TException)
			{
				throw new TException();
			}
			else if(cause instanceof NoSuchAlgorithmException)
			{
				throw new NoSuchAlgorithmException();
			}
			else if(cause instanceof InvalidKeyException)
			{
				throw new InvalidKeyException();
			}
			else if(cause instanceof SignatureException)
			{
				throw new SignatureException();
			}
		}
	}
}