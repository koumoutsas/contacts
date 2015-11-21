package com.kareebo.contacts.client;

import com.kareebo.contacts.client.service.ResultHandler;
import com.kareebo.contacts.crypto.Utils;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.Notification;
import com.kareebo.contacts.thrift.NotificationMethod;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import org.apache.thrift.TBase;
import org.apache.thrift.TSerializer;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TClientTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.vertx.java.core.impl.DefaultVertx;

import java.security.*;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link NotificationHandler}
 */
public class NotificationHandlerTest
{
	final private static SigningKey signingKeyExpected=new SigningKey(privateKey(),SignatureAlgorithm.SHA256withECDSAprime239v1);
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
	final private NotificationHandler notificationHandler=new NotificationHandler(clientManagerExpected,signingKeyExpected,clientIdExpected);
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	private static PrivateKey privateKey()
	{
		try
		{
			Security.addProvider(new BouncyCastleProvider());
			final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
			final KeyPairGenerator g=KeyPairGenerator.getInstance("ECDSA",Utils.getProvider());
			g.initialize(ecSpec,new SecureRandom());
			return g.generateKeyPair().getPrivate();
		}
		catch(NoSuchAlgorithmException|NoSuchProviderException|InvalidAlgorithmParameterException e)
		{
			return null;
		}
	}

	@Test
	public void testHandle() throws Exception
	{
		final boolean[] error=new boolean[1];
		notificationHandler.handle(new TSerializer().serialize(new Notification(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method3,0)),new ResultHandler<TBase>()
		{
			@Override
			public void handleError(final Throwable cause)
			{
				error[0]=true;
			}

			@Override
			public void handle(final TBase event)
			{
				error[0]=false;
			}
		});
		assertTrue(error[0]);
	}

	@Test
	public void testInvalidService() throws Exception
	{
		final String unknownService="a";
		thrown.expect(NotificationHandler.InvalidServiceMethod.class);
		thrown.expectMessage("Unknown service "+unknownService);
		notificationHandler.handle(new TSerializer().serialize(new Notification(new NotificationMethod(unknownService,""),0)),null);
	}

	@Test
	public void testInvalidMethod() throws Exception
	{
		final String unknownMethod="b";
		final String service=com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method3.getServiceName();
		thrown.expect(NotificationHandler.InvalidServiceMethod.class);
		thrown.expectMessage("Unknown method "+service+'.'+unknownMethod);
		notificationHandler.handle(new TSerializer().serialize(new Notification(new NotificationMethod(service,unknownMethod),0)),null);
	}
}