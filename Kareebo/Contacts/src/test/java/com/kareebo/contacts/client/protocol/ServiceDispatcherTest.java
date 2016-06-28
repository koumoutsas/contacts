package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateJob;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.crypto.TestSignatureKeyPair;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TClientTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.vertx.java.core.impl.DefaultVertx;

import javax.annotation.Nonnull;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ServiceDispatcher}
 */
public class ServiceDispatcherTest
{
	private static final String serviceName=ServiceDispatcherTest.class.getSimpleName()+"$"+ServiceImplementation.class.getSimpleName();
	private static final String serviceName1=ServiceImplementation1.class.getName();
	private final static long notificationIdExpected=0L;
	private final static long notificationIdExpected1=1L;
	private final static EnqueuerImplementation enqueuerExpected=new EnqueuerImplementation();
	private final static Enqueuers enqueuers=new Enqueuers(enqueuerExpected,enqueuerExpected);
	final private static SigningKey signingKeyExpected;
	final private static ClientId clientIdExpected=new ClientId();
	final private static TAsyncClientManager clientManagerExpected=new TAsyncClientManager(new TClientTransport(new TClientTransport.AbstractArgs
		                                                                                                                (new DefaultVertx())
	{
	})
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
	},(TProtocolFactory)tTransport->null);
	final private static ServiceMethod invalid=new ServiceMethod(ServiceImplementation.method1.getServiceName(),"b");

	static
	{
		try
		{
			signingKeyExpected=new TestSignatureKeyPair().signingKey();
		}
		catch(InvalidKeySpecException|NoSuchAlgorithmException|InvalidAlgorithmParameterException|NoSuchProviderException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Rule
	final public ExpectedException thrown=ExpectedException.none();

	@Test(expected=ClassNotFoundException.class)
	public void testAddUnknownService() throws Exception
	{
		new ServiceDispatcher(enqueuers,signingKeyExpected,clientIdExpected).add("random",clientManagerExpected);
	}

	@Test
	public void testAddDuplicateService() throws Exception
	{
		final ServiceDispatcher serviceDispatcher=new ServiceDispatcher(enqueuers,signingKeyExpected,clientIdExpected);
		serviceDispatcher.add(serviceName,clientManagerExpected);
		thrown.expect(ServiceDispatcher.DuplicateService.class);
		serviceDispatcher.add(serviceName,clientManagerExpected);
	}

	@Test(expected=com.kareebo.contacts.client.jobs.ServiceDispatcher.NoSuchService.class)
	public void testUnknownService() throws Exception
	{
		new ServiceDispatcher(enqueuers,signingKeyExpected,clientIdExpected).run(ServiceImplementation.method1,notificationIdExpected);
	}

	@Test
	public void testClear() throws Exception
	{
		final ServiceDispatcher serviceDispatcher=new ServiceDispatcher(enqueuers,signingKeyExpected,clientIdExpected);
		serviceDispatcher.add(serviceName,clientManagerExpected);
		serviceDispatcher.run(ServiceImplementation.method1,notificationIdExpected);
		serviceDispatcher.clear();
		thrown.expect(com.kareebo.contacts.client.jobs.ServiceDispatcher.NoSuchService.class);
		serviceDispatcher.run(ServiceImplementation.method1,notificationIdExpected);
	}

	@Test
	public void testRun() throws Exception
	{
		final ServiceDispatcher serviceDispatcher=new ServiceDispatcher(enqueuers,signingKeyExpected,clientIdExpected);
		serviceDispatcher.add(serviceName,clientManagerExpected);
		serviceDispatcher.add(serviceName1,clientManagerExpected);
		serviceDispatcher.run(ServiceImplementation.method1,notificationIdExpected);
		checkNotification(notificationIdExpected,ServiceImplementation.method1);
		serviceDispatcher.run(ServiceImplementation1.method1,notificationIdExpected1);
		checkNotification(notificationIdExpected1,ServiceImplementation1.method1);
		serviceDispatcher.run(ServiceImplementation.method1,new LongId(notificationIdExpected),null);
		checkNotification(notificationIdExpected,ServiceImplementation.method1);
		thrown.expect(Service.NoSuchMethod.class);
		serviceDispatcher.run(invalid,notificationIdExpected);
	}

	private void checkNotification(final long notificationIdExpected,final ServiceMethod method)
	{
		assertTrue(enqueuerExpected.hasJob(JobType.Processor,method,new LongId(notificationIdExpected)));
	}

	private static class MyAsyncClient extends TAsyncClient
	{
		MyAsyncClient(final TAsyncClientManager clientManager)
		{
			super(clientManager);
		}
	}

	private static class ServiceImplementation extends Service<MyAsyncClient>
	{
		final static ServiceMethod method1=new ServiceMethod(ServiceImplementation.class.getName().substring(ServiceImplementation.class.getPackage().getName().length()+1),"1");
		LongId notificationIdExpected=new LongId(ServiceDispatcherTest.notificationIdExpected);
		ServiceMethod methodExpected=method1;

		ServiceImplementation(final Context context,final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId
			                                                                                                                     clientId)
		{
			super(context,asyncClientManager,signingKey,clientId);
		}

		@Override
		protected MyAsyncClient construct(@Nonnull final TAsyncClientManager asyncClientManager)
		{
			return new MyAsyncClient(asyncClientManager);
		}

		@Nonnull
		@Override
		protected ServiceMethod[] methodNames()
		{
			return new ServiceMethod[]{method1};
		}

		@Nonnull
		@Override
		protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
		{
			return new com.kareebo.contacts.client.jobs.Service.Functor[]{new Functor<TBase>()
			{
				@Override
				protected void runInternal(@Nonnull final MyAsyncClient asyncClient,@Nonnull final TBase payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					assertEquals(notificationIdExpected,payload);
					assertEquals(enqueuerExpected,intermediateResultEnqueuer);
					assertEquals(enqueuerExpected,finalResultEnqueuer);
					enqueuerExpected.put(new IntermediateJob(JobType.Processor,method1,context,payload));
					enqueuerExpected.put(new IntermediateJob(JobType.Processor,methodExpected,context,payload));
				}
			}};
		}
	}

	private static class ServiceImplementation1 extends ServiceImplementation
	{
		final static ServiceMethod method1=new ServiceMethod(ServiceImplementation1.class.getName().substring(ServiceImplementation1.class
			                                                                                                      .getPackage().getName().length()+1),"1");

		ServiceImplementation1(final Context context,final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
		{
			super(context,asyncClientManager,signingKey,clientId);
			notificationIdExpected=new LongId(ServiceDispatcherTest.notificationIdExpected1);
			methodExpected=method1;
		}
	}
}
