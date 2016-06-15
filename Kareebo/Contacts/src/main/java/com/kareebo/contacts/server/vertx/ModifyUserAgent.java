package com.kareebo.contacts.server.vertx;

import com.kareebo.contacts.server.handler.Configuration;
import org.apache.thrift.TProcessor;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link Service} for {@link com.kareebo.contacts.server.handler.ModifyUserAgent}
 */
class ModifyUserAgent implements Service
{
	final private Configuration configuration;

	public ModifyUserAgent(final @Nonnull Configuration configuration)
	{
		this.configuration=configuration;
	}

	@Nonnull
	@Override
	public TProcessor create()
	{
		return new com.kareebo.contacts.thrift.ModifyUserAgent.AsyncProcessor<>(new com.kareebo.contacts.server.handler.ModifyUserAgent
			                                                                        (configuration));
	}
}
