package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.crypto.TestSignatureKeyPair;
import com.kareebo.contacts.thrift.*;
import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncMethodCall;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Base class for simple tests
 */
class SimpleTestHarness
{
	void test(final List<TestBase> tests) throws com.kareebo.contacts.client.jobs.ServiceDispatcher.NoSuchService, com.kareebo.contacts.client.jobs.Service.ExecutionFailed, com.kareebo.contacts.client.jobs.Service.NoSuchMethod, ServiceDispatcher.DuplicateService, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException
	{
		for(final TestBase test : tests)
		{
			test.run();
		}
	}

	abstract static class TestBase<T,E>
	{
		protected final SignatureAlgorithm algorithm=SignatureAlgorithm.SHA512withECDSAprime239v1;
		protected final ClientId clientId=new ClientId(0,0);
		final TestSignatureKeyPair testSignatureKeyPair;
		final String fieldName;
		final private EnqueuerImplementation enqueuer=new EnqueuerImplementation();

		TestBase(final String fieldName) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
		{
			testSignatureKeyPair=new TestSignatureKeyPair();
			this.fieldName=fieldName;
		}

		void run() throws com.kareebo.contacts.client.jobs.ServiceDispatcher.NoSuchService, com.kareebo.contacts.client.jobs.Service.NoSuchMethod, com.kareebo.contacts.client.jobs.Service.ExecutionFailed, ClassNotFoundException, ServiceDispatcher.DuplicateService, InvalidKeySpecException, NoSuchAlgorithmException
		{
			test(true);
			test(false);
		}

		private void test(final boolean success) throws com.kareebo.contacts.client.jobs.Service.ExecutionFailed, com.kareebo.contacts.client.jobs.ServiceDispatcher.NoSuchService, com.kareebo.contacts.client.jobs.Service.NoSuchMethod, ServiceDispatcher.DuplicateService, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException
		{
			final ServiceMethod method=getServiceMethod();
			final ServiceDispatcher serviceDispatcher=new ServiceDispatcher(new Enqueuers(enqueuer,enqueuer),testSignatureKeyPair.signingKey(),
				                                                               clientId);
			serviceDispatcher.add(method.getServiceName(),clientManager(success));
			serviceDispatcher.run(method,constructPayload(),null);
			final ServiceMethod nextMethod=nextServiceMethod();
			if(success)
			{
				if(nextMethod==null)
				{
					assertTrue(enqueuer.initialState());
				}
				else
				{
					if(isFinal())
					{
						assertTrue(enqueuer.isSuccess(JobType.Protocol,new ServiceMethod(method.getServiceName(),null),SuccessCode.Ok));
					}
					else
					{
						assertTrue(enqueuer.hasJob(JobType.Processor,nextMethod,null));
					}
				}
			}
			else
			{
				assertTrue(enqueuer.isError(JobType.Protocol,getServiceMethod(),ErrorCode.Failure));
			}
		}

		abstract protected ServiceMethod getServiceMethod();

		abstract MyClientManager<T,E> clientManager(final boolean success);

		abstract TBase constructPayload();

		/// Override this method to return null if the result handler is {@link IntermediateVoidResultHandler}
		protected ServiceMethod nextServiceMethod()
		{
			return getServiceMethod();
		}

		protected boolean isFinal()
		{
			return false;
		}

		abstract class MyClientManager<B,C> extends MockClientManager<C>
		{
			protected TAsyncMethodCall method;

			MyClientManager(final boolean succeed)
			{
				super(succeed);
			}

			@Override
			protected void callInternal(final TAsyncMethodCall method)
			{
				this.method=method;
				try
				{
					final Field field=cls.getDeclaredField(fieldName);
					field.setAccessible(true);
					//noinspection unchecked
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
						assertTrue(new Verifier(testSignatureKeyPair.getPublic(),algorithm).verify(tPayload,signature.getBuffer()));
					}
				}
			};
		}
	}

	abstract static class HashBufferTestBase<E> extends SimpleTestBase<HashBuffer,E>
	{
		HashBufferTestBase(final String fieldName) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
		{
			super(fieldName);
		}

		@Override
		protected HashBuffer constructPayload()
		{
			final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
			buffer.mark();
			return new HashBuffer(buffer,HashAlgorithm.Fake);
		}
	}

	abstract static class LongIdTestBase<E> extends SimpleTestBase<LongId,E>
	{
		LongIdTestBase(final String fieldName) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
		{
			super(fieldName);
		}

		@Override
		protected LongId constructPayload()
		{
			return new LongId(8);
		}
	}

	abstract static class SimpleTestBase<T extends TBase,E> extends TestBase<T,E>
	{
		SimpleTestBase(final String fieldName) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
		{
			super(fieldName);
		}

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
					assertTrue(new Verifier(testSignatureKeyPair.getPublic(),algorithm).verify(payload,signature.getBuffer()));
				}
			};
		}
	}
}