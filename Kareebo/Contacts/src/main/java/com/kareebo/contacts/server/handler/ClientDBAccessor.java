package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.IdPair;
import com.kareebo.contacts.thrift.InvalidArgument;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.util.Map;

/**
 * Base class for all handlers that operate at the client level
 */
class ClientDBAccessor
{
	/**
	 * Restricts the query fields to the clients only
	 */
	private static final String[] queryFields={"clients"};
	/**
	 * The datastore is initialized in the ctor and is never null
	 */
	private DataStore<Long,User> dataStore;
	/**
	 * Caches of intermediate structures used to get to the client.
	 */
	private User user;
	private IdPair idPair;
	private Map<CharSequence,Client> clients;

	/**
	 * Sets the datastore
	 */
	ClientDBAccessor()
	{
		try
		{
			dataStore=DataStoreFactory.getDataStore(Long.class,User.class,
				                                       new Configuration());
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Get a client. Needs to be called before set
	 *
	 * @param idPair The user and client ids
	 * @return The client
	 * @throws InvalidArgument When the user or client cannot be found in the DB
	 */
	Client get(final IdPair idPair) throws InvalidArgument
	{
		user=dataStore.get(idPair.getUserId(),queryFields);
		if(user==null)
		{
			throw new InvalidArgument();
		}
		clients=user.getClients();
		if(clients==null)
		{
			user=null;
			throw new InvalidArgument();
		}
		final Client client=clients.get(TypeConverter.convert(idPair.getClientId()));
		if(client==null)
		{
			user=null;
			clients=null;
			throw new InvalidArgument();
		}
		this.idPair=idPair;
		return client;
	}

	void set(final Client client)
	{
		if(user==null||clients==null)
		{
			throw new IllegalStateException();
		}
		if(client==null)
		{
			throw new IllegalArgumentException();
		}
		clients.put(TypeConverter.convert(idPair.getClientId()),client);
		user.setClients(clients);
		dataStore.put(idPair.getUserId(),user);
	}

	void close()
	{
		dataStore.close();
	}
}
