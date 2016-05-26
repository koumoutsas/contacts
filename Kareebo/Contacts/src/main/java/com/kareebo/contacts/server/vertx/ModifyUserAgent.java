package com.kareebo.contacts.server.vertx;

import com.kareebo.contacts.server.gora.User;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TProcessor;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link Service} for {@link com.kareebo.contacts.server.handler.ModifyUserAgent}
 */
class ModifyUserAgent implements Service
{
	final private DataStore<Long,User> datastore;

	ModifyUserAgent(final @Nonnull DataStore<Long,User> datastore)
	{
		this.datastore=datastore;
	}

	@Nonnull
	@Override
	public TProcessor create()
	{
		return new com.kareebo.contacts.thrift.ModifyUserAgent.AsyncProcessor<>(new com.kareebo.contacts.server.handler.ModifyUserAgent
			                                                                        (datastore));
	}
}
