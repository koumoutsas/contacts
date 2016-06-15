package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import org.apache.gora.store.DataStore;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * Wrapper around all needed objects for configuring a service
 */
public class Configuration
{
	private final DataStore<Long,User> userDataStore;
	private final DataStore<ByteBuffer,HashIdentity> identityDatastore;
	private final ClientNotifier clientNotifier;
	private final GraphAccessor graphAccessor;

	public Configuration(final @Nonnull DataStore<Long,User> userDataStore,final @Nonnull DataStore<ByteBuffer,HashIdentity> identityDatastore,
	                     @Nonnull final ClientNotifier clientNotifier,final @Nonnull GraphAccessor graphAccessor)
	{
		this.userDataStore=userDataStore;
		this.identityDatastore=identityDatastore;
		this.clientNotifier=clientNotifier;
		this.graphAccessor=graphAccessor;
	}

	@Nonnull DataStore<Long,User> getUserDataStore()
	{
		return userDataStore;
	}

	@Nonnull DataStore<ByteBuffer,HashIdentity> getIdentityDatastore()
	{
		return identityDatastore;
	}

	@Nonnull ClientNotifier getClientNotifier()
	{
		return clientNotifier;
	}

	@Nonnull GraphAccessor getGraphAccessor()
	{
		return graphAccessor;
	}
}
