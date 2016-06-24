package com.kareebo.contacts.server.handler;

/**
 * Dummy service handler for {@link com.kareebo.contacts.server.vertx.Verticle} tests
 */
@SuppressWarnings("unused")
public class VerticleDummyHandler implements com.kareebo.contacts.thrift.VerticleDummyHandler.AsyncIface
{
	public VerticleDummyHandler(@SuppressWarnings("UnusedParameters") final Configuration handlerConfiguration)
	{
	}
}
