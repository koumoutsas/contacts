package com.kareebo.contacts.client.externalServices;

import com.kareebo.contacts.thrift.client.ClientConstants;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Protocol}
 */
public class ProtocolTest
{
	static
	{
		// For 100% coverage
		new Protocol();
	}

	@Test(expected=Protocol.AlreadyRegistered.class)
	public void registerProtocol() throws IOException, Protocol.AlreadyRegistered
	{
		try
		{
			Protocol.registerProtocol(new TestConnector());
		}
		catch(Protocol.AlreadyRegistered ignored)
		{
		}
		final URL url=new URL(ClientConstants.Protocol,"host","");
		url.openConnection().connect();
		assertTrue(TestConnector.isConnected());
		TestConnector.disconnect();
		Protocol.registerProtocol(new TestConnector());
	}

	static class TestConnector implements Protocol.Connector
	{
		private static final Object lock=new Object();
		private static boolean connected;

		static boolean isConnected()
		{
			return connected;
		}

		static void disconnect()
		{
			synchronized(lock)
			{
				connected=false;
			}
		}

		@Override
		public void connect(@Nonnull final URL url) throws IOException
		{
			synchronized(lock)
			{
				connected=true;
			}
		}
	}
}