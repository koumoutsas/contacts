package com.kareebo.contacts.client.externalServices;

import com.kareebo.contacts.thrift.client.ClientConstants;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Handler for the application protocol
 */
class Protocol
{
	private static final Object streamHandlerLock=new Object();
	private static boolean set;

	/**
	 * Register {@link ClientConstants#Protocol}. Wraps {@link URL#setURLStreamHandlerFactory(URLStreamHandlerFactory)} so that if called
	 * multiple times, it throws a checked exception
	 *
	 * @param connector A lambda of {@link Connector}
	 * @throws AlreadyRegistered If the method has been already called
	 */
	static void registerProtocol(@Nonnull final Connector connector) throws AlreadyRegistered
	{
		synchronized(streamHandlerLock)
		{
			if(set)
			{
				throw new AlreadyRegistered();
			}
			URL.setURLStreamHandlerFactory(protocol->ClientConstants.Protocol.equals(protocol)?new URLStreamHandler()
			{
				protected URLConnection openConnection(URL url) throws IOException
				{
					return new URLConnection(url)
					{
						public void connect() throws IOException
						{
							connector.connect(url);
						}
					};
				}
			}:null);
			set=true;
		}
	}

	/**
	 * Handles the {@link URL}s of the application protocol
	 */
	@FunctionalInterface
	interface Connector
	{
		/**
		 * Implements {@link URLConnection#connect()}
		 *
		 * @param url The handled {@link URL}
		 * @throws IOException
		 */
		void connect(@Nonnull final URL url) throws IOException;
	}

	static class AlreadyRegistered extends Exception
	{
	}
}
