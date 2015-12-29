package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TClientTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.vertx.java.core.impl.DefaultVertx;

import java.security.PrivateKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ServiceDispatcher}
 */
public class ServiceDispatcherTest
{
	final static long notificationIdExpected=0L;
	final static EnqueuerImplementation enqueuerExpected=new EnqueuerImplementation();
	final static Enqueuers enqueuers=new Enqueuers(JobType.Processor,enqueuerExpected,enqueuerExpected);
	final private static SigningKey signingKeyExpected=new SigningKey(new PrivateKey()
	{
		@Override
		public String getAlgorithm()
		{
			return null;
		}

		@Override
		public String getFormat()
		{
			return null;
		}

		@Override
		public byte[] getEncoded()
		{
			return new byte[0];
		}
	},SignatureAlgorithm.Fake);
	final private static ClientId clientIdExpected=new ClientId();
	final private static TAsyncClientManager clientManagerExpected=new TAsyncClientManager(new TClientTransport(new TClientTransport.AbstractArgs
		                                                                                                                (new DefaultVertx())
	{
		@Override
		public TClientTransport.AbstractArgs setTimeout(final long timeout)
		{
			return super.setTimeout(timeout);
		}
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
	},new TProtocolFactory()
	{
		@Override
		public TProtocol getProtocol(final TTransport tTransport)
		{
			return null;
		}
	});
	final private static ServiceMethod invalid=new ServiceMethod(ServiceImplementation.method1.getServiceName(),"b");
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testRun() throws Exception
	{
		final ServiceDispatcher serviceDispatcher=new ServiceDispatcher(enqueuers,clientManagerExpected,signingKeyExpected,
			                                                               clientIdExpected);
		serviceDispatcher.run(ServiceImplementation.method1,notificationIdExpected);
		final LongId longId=new LongId(notificationIdExpected);
		assertTrue(enqueuerExpected.hasJob(JobType.Processor,ServiceImplementation.method1,longId));
		serviceDispatcher.run(ServiceImplementation.method1,longId,null);
		assertTrue(enqueuerExpected.hasJob(JobType.Processor,ServiceImplementation.method1,longId));
		thrown.expect(Service.NoSuchMethod.class);
		serviceDispatcher.run(invalid,notificationIdExpected);
	}

	private static class MyAsyncClient extends TAsyncClient
	{
		public MyAsyncClient(final TAsyncClientManager clientManager)
		{
			super(clientManager);
		}
	}

	public static class ServiceImplementation extends Service<MyAsyncClient>
	{
		final static ServiceMethod method1=new ServiceMethod(ServiceImplementation.class.getName().substring(ServiceImplementation.class.getPackage().getName().length()+1),"1");

		ServiceImplementation(final Context context,final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId
			                                                                                                                     clientId)
		{
			super(context,asyncClientManager,signingKey,clientId);
		}

		@Override
		protected MyAsyncClient construct(final TAsyncClientManager asyncClientManager)
		{
			return new MyAsyncClient(asyncClientManager);
		}

		@Override
		protected ServiceMethod[] methodNames()
		{
			return new ServiceMethod[]{method1};
		}

		@Override
		protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
		{
			return new com.kareebo.contacts.client.jobs.Service.Functor[]{new Functor<TBase>()
			{
				@Override
				protected void runInternal(final MyAsyncClient asyncClient,final TBase payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					assertEquals(notificationIdExpected,((LongId)payload).getId());
					assertEquals(enqueuerExpected,intermediateResultEnqueuer);
					assertEquals(enqueuerExpected,finalResultEnqueuer);
					enqueuerExpected.enqueue(JobType.Processor,method1,context,payload);
					if(!method1.equals(ServiceImplementation.method1))
					{
						throw new NoSuchMethod();
					}
				}
			}};
		}
	}
}
