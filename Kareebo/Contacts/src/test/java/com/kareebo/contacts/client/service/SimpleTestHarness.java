package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncMethodCall;
import org.junit.Test;

import java.lang.reflect.Field;
import java.security.*;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Base class for simple tests
 */
class SimpleTestHarness
{
	void test(final List<TestBase> tests) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		for(final TestBase test : tests)
		{
			test.run();
		}
	}

	abstract static class TestBase<T,E>
	{
		protected final SignatureAlgorithm algorithm=SignatureAlgorithm.SHA256withECDSAprime239v1;
		protected final KeyPair keyPair;
		protected final String fieldName;
		private final ClientId clientId=new ClientId(0,0);
		private final MockHandler<E> handler=new MockHandler<>();

		TestBase(final String fieldName) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
		{
			keyPair=new KeyPair();
			this.fieldName=fieldName;
		}

		@Test
		public void run() throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
		{
			test(true);
			test(false);
		}

		private void test(final boolean success) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
		{
			perform(clientManager(success),new SigningKey(keyPair.getPrivate(),algorithm),clientId,new AsyncResultHandler<>(handler));
			assertEquals(success,handler.succeeded);
		}

		abstract void perform(final MyClientManager<T,E> clientManager,final SigningKey signingKey,final ClientId clientId,final
		AsyncResultHandler<E>
			                                                                                                                   handler) throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException;

		abstract MyClientManager<T,E> clientManager(final boolean success);

		abstract class MyClientManager<B,C> extends MockClientManager<C>
		{
			protected TAsyncMethodCall method;

			public MyClientManager(final boolean succeed)
			{
				super(succeed);
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void callInternal(final TAsyncMethodCall method)
			{
				this.method=method;
				try
				{
					final Field field=cls.getDeclaredField(fieldName);
					field.setAccessible(true);
					handlePayload((B)field.get(method));
					field.setAccessible(false);
					resultHandler.handle(new MockResult<C>(succeed)
					{
						@Override
						public C result()
						{
							return null;
						}
					});
				}
				catch(Exception e)
				{
					e.printStackTrace();
					fail();
				}
			}

			abstract void handlePayload(final B payload) throws NoSuchFieldException, NoSuchAlgorithmException, TException,
				                                                    NoSuchProviderException, InvalidKeyException, SignatureException, IllegalAccessException;
		}
	}

	abstract static class CollectionSimpleTestBase<T extends TBase,E> extends TestBase<Collection<T>,E>
	{
		private final String collectionFieldName;

		CollectionSimpleTestBase(final String fieldName,final String collectionFieldName) throws InvalidAlgorithmParameterException,
			                                                                                         NoSuchAlgorithmException, NoSuchProviderException
		{
			super(fieldName);
			this.collectionFieldName=collectionFieldName;
		}

		@Override
		MyClientManager<Collection<T>,E> clientManager(final boolean success)
		{
			return new MyClientManager<Collection<T>,E>(success)
			{
				@Override
				void handlePayload(final Collection<T> payload) throws NoSuchFieldException, IllegalAccessException, NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
				{
					for(final T t : payload)
					{
						final Class tClass=t.getClass();
						final Field signatureField=tClass.getDeclaredField("signature");
						signatureField.setAccessible(true);
						final SignatureBuffer signature=(SignatureBuffer)signatureField.get(t);
						signatureField.setAccessible(false);
						final Field tPayloadField=tClass.getDeclaredField(collectionFieldName);
						tPayloadField.setAccessible(true);
						final TBase tPayload=(TBase)tPayloadField.get(t);
						tPayloadField.setAccessible(false);
						assertTrue(new Verifier(keyPair.getPublic(),algorithm).verify(tPayload,signature.getBuffer()));
					}
				}
			};
		}
	}

	abstract static class SimpleTestBase<T extends TBase,E> extends TestBase<T,E>
	{
		SimpleTestBase(final String fieldName) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
		{
			super(fieldName);
		}

		@Override
		void perform(final MyClientManager<T,E> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<E>
			                                                                                                          handler) throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
		{
			perform(construct(),clientManager,signingKey,clientId,handler);
		}

		abstract protected void perform(final T object,final MockClientManager<E> clientManager,final SigningKey signingKey,final ClientId
			                                                                                                                    clientId,
		                                final
		                                AsyncResultHandler<E> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException,
			                                                                      InvalidKeyException, SignatureException;

		abstract protected T construct();

		@Override
		MyClientManager<T,E> clientManager(final boolean success)
		{
			return new MyClientManager<T,E>(success)
			{
				@Override
				void handlePayload(final T payload) throws NoSuchFieldException, NoSuchAlgorithmException, TException,
					                                           NoSuchProviderException, InvalidKeyException, SignatureException, IllegalAccessException
				{
					final Field signatureField=cls.getDeclaredField("signature");
					signatureField.setAccessible(true);
					final SignatureBuffer signature=(SignatureBuffer)signatureField.get(method);
					signatureField.setAccessible(false);
					assertTrue(new Verifier(keyPair.getPublic(),algorithm).verify(payload,signature.getBuffer()));
				}
			};
		}
	}
}