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

import static org.junit.Assert.*;

/**
 * Base class for simple tests
 */
abstract public class SimpleTestBase<T extends TBase>
{
	private final SignatureAlgorithm algorithm=SignatureAlgorithm.SHA256withECDSAprime239v1;
	private final ClientId clientId=new ClientId(0,0);
	private final KeyPair keyPair;
	private final MockHandler<Void> handler=new MockHandler<>();

	public SimpleTestBase() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
		keyPair=new KeyPair();
	}

	@Test
	public void performTest() throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
	{
		test(true);
		test(false);
	}

	/**
	 * Test that the signature is correct and that the correct result handler method is called
	 */
	private void test(final boolean success) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		perform(construct(),new MyClientManager(success),new SigningKey(keyPair.getPrivate(),algorithm),clientId,new AsyncResultHandler<>(handler));
		assertEquals(success,handler.succeeded);
	}

	abstract void perform(final T object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId clientId,final
	AsyncResultHandler<Void> asyncResultHandler) throws NoSuchProviderException, TException, NoSuchAlgorithmException,
		                                                    InvalidKeyException, SignatureException;

	abstract protected T construct();

	abstract protected String getFieldName();

	private class MyClientManager extends MockClientManager<Void>
	{
		public MyClientManager(final boolean success)
		{
			super(success);
		}

		@Override
		void callInternal(final TAsyncMethodCall method)
		{
			try
			{
				final Field field=cls.getDeclaredField(getFieldName());
				field.setAccessible(true);
				final Field signatureField=cls.getDeclaredField("signature");
				signatureField.setAccessible(true);
				final TBase payload=(TBase)field.get(method);
				final SignatureBuffer signature=(SignatureBuffer)signatureField.get(method);
				assertTrue(new Verifier(keyPair.getPublic(),algorithm).verify(payload,signature.getBuffer()));
				resultHandler.handle(new MockVoidResult(succeed));
				field.setAccessible(false);
				signatureField.setAccessible(false);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fail();
			}
		}
	}
}
