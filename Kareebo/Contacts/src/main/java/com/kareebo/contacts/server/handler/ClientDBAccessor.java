package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.IdPair;
import com.kareebo.contacts.thrift.InvalidArgument;
import org.apache.gora.store.DataStore;

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
	 * The datastore is initialized in the constructor and is never null
	 */
	private DataStore<Long,User> dataStore;
	/**
	 * Caches of intermediate structures used to get to the client.
	 */
	User user;
	private IdPair idPair;
	private Map<CharSequence,Client> clients;

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	ClientDBAccessor(final DataStore<Long,User> dataStore)
	{
		this.dataStore=dataStore;
	}

	/**
	 * Get a client. Needs to be called before set
	 *
	 * @param idPair The user and client ids
	 * @return The client
	 * @throws InvalidArgument If there is no such client
	 */
	Client get(final IdPair idPair) throws InvalidArgument
	{
		getClients(idPair);
		final Client client=clients.get(TypeConverter.convert(idPair.getClientId()));
		if(client==null)
		{
			resetState();
			throw new InvalidArgument();
		}
		return client;
	}

	private void getClients(final IdPair idPair) throws InvalidArgument
	{
		this.idPair=idPair;
		user=dataStore.get(idPair.getUserId(),queryFields);
		if(user==null)
		{
			resetState();
			throw new InvalidArgument();
		}
		clients=user.getClients();
	}

	private void resetState()
	{
		user=null;
		idPair=null;
		clients=null;
	}

	/**
	 * Set a client for a user, without calling get first. The client can exist already, in
	 * which case it's an update, or not, in which case it's an insert.
	 *
	 * @param idPair The ids
	 * @param client The client
	 * @throws InvalidArgument When the user cannot be found in the DB
	 */
	void put(final IdPair idPair,final Client client) throws InvalidArgument
	{
		getClients(idPair);
		put(client);
	}

	/**
	 * Set a client for a user that has been retrieved before with get. The client can exist already, in
	 * which case it's an update, or not, in which case it's an insert.
	 *
	 * @param client The client
	 */
	void put(final Client client)
	{
		if(user==null||clients==null||idPair==null)
		{
			throw new IllegalStateException();
		}
		clients.put(TypeConverter.convert(idPair.getClientId()),client);
		user.setClients(clients);
		dataStore.put(idPair.getUserId(),user);
	}

	/**
	 * Call this method to commit to the DB
	 */
	void close()
	{
		dataStore.close();
	}
}
