package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.ServiceMethod;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
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
	final private static String serviceName=ServiceImplementation.class.getName().substring(ServiceImplementation.class.getPackage().getName().length()+1);
	final private static ServiceMethod valid=new ServiceMethod(serviceName,"a");
	final private static ServiceMethod invalid=new ServiceMethod(serviceName,"b");
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testRun() throws Exception
	{
		final ServiceDispatcher serviceDispatcher=new ServiceDispatcher(enqueuerExpected,clientManagerExpected,signingKeyExpected,
			                                                               clientIdExpected);
		serviceDispatcher.run(valid,notificationIdExpected);
		final LongId longId=new LongId(notificationIdExpected);
		assertTrue(enqueuerExpected.job(valid,longId));
		serviceDispatcher.run(valid,longId);
		assertTrue(enqueuerExpected.job(valid,longId));
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
		ServiceImplementation(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
		{
			super(asyncClientManager,signingKey,clientId);
		}

		@Override
		protected MyAsyncClient construct(final TAsyncClientManager asyncClientManager)
		{
			return new MyAsyncClient(asyncClientManager);
		}

		@Override
		protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
		{
			assertEquals(notificationIdExpected,((LongId)payload).getId());
			assertEquals(enqueuerExpected,enqueuer);
			enqueuer.processor(method,payload);
			if(!method.equals(valid))
			{
				throw new NoSuchMethod();
			}
		}
	}
}
