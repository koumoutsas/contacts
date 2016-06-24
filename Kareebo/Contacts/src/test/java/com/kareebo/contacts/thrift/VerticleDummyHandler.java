package com.kareebo.contacts.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;

/**
 * Dummy service handler for {@link com.kareebo.contacts.server.vertx.Verticle} tests
 */
abstract public class VerticleDummyHandler
{
	public interface AsyncIface
	{
	}

	public static class AsyncProcessor implements TProcessor
	{
		public AsyncProcessor(@SuppressWarnings("UnusedParameters") final AsyncIface asyncIface)
		{
		}

		@Override
		public boolean process(final TProtocol tProtocol,final TProtocol tProtocol1) throws TException
		{
			return false;
		}

		@Override
		public boolean isAsyncProcessor()
		{
			return false;
		}
	}
}
