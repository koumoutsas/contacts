package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.NotificationMethod;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
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

import java.security.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ServiceFactory}
 */
public class ServiceFactoryTest
{
	final static long notificationIdExpected=0L;
	final static AsyncResultHandler<TBase> handlerExpected=new AsyncResultHandler<>(new ResultHandler<TBase>()
	{
		@Override
		public void handleError(final Throwable cause)
		{
		}

		@Override
		public void handle(final TBase event)
		{
		}
	});
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
	final private static NotificationMethod valid=new NotificationMethod(serviceName,"a");
	final private static NotificationMethod invalid=new NotificationMethod(serviceName,"b");
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testRun() throws Exception
	{
		// For 100% test coverage
		new ServiceFactory();
		ServiceFactory.run(clientManagerExpected,signingKeyExpected,clientIdExpected,valid,notificationIdExpected,handlerExpected);
		thrown.expect(ServiceFactory.NoSuchMethod.class);
		ServiceFactory.run(clientManagerExpected,signingKeyExpected,clientIdExpected,invalid,notificationIdExpected,handlerExpected);
	}

	@Test
	public void testNoSuchMethod() throws Exception
	{
		thrown.expect(ServiceFactory.NoSuchService.class);
		ServiceFactory.run(clientManagerExpected,signingKeyExpected,clientIdExpected,new NotificationMethod("",""),notificationIdExpected,
			handlerExpected);
	}

	public static class ServiceImplementation implements Service
	{
		public ServiceImplementation(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
		{
			assertEquals(clientManagerExpected,asyncClientManager);
			assertEquals(signingKeyExpected,signingKey);
			assertEquals(clientIdExpected,clientId);
		}

		@Override
		public void run(final NotificationMethod method,final long notificationId,final AsyncResultHandler<TBase> handler) throws NoSuchMethod, NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
		{
			assertEquals(notificationIdExpected,notificationId);
			assertEquals(handlerExpected,handler);
			if(!method.equals(valid))
			{
				throw new NoSuchMethod();
			}
		}
	}
}