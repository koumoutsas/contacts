package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
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
	private ClientId clientId;
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
	 * @param clientId The user and client ids
	 * @return The client
	 * @throws FailedOperation If there is no such client
	 */
	Client get(final ClientId clientId) throws FailedOperation
	{
		getClients(clientId);
		final Client client=clients.get(TypeConverter.convert(clientId.getClient()));
		if(client==null)
		{
			resetState();
			throw new FailedOperation();
		}
		return client;
	}

	private void getClients(final ClientId clientId) throws FailedOperation
	{
		this.clientId=clientId;
		user=dataStore.get(clientId.getUser(),queryFields);
		if(user==null)
		{
			resetState();
			throw new FailedOperation();
		}
		clients=user.getClients();
	}

	private void resetState()
	{
		user=null;
		clientId=null;
		clients=null;
	}

	/**
	 * Set a client for a user, without calling get first. The client can exist already, in
	 * which case it's an update, or not, in which case it's an insert.
	 *
	 * @param clientId The ids
	 * @param client The client
	 * @throws FailedOperation When the user cannot be found in the DB
	 */
	void put(final ClientId clientId,final Client client) throws FailedOperation
	{
		getClients(clientId);
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
		if(user==null||clients==null||clientId==null)
		{
			throw new IllegalStateException();
		}
		clients.put(TypeConverter.convert(clientId.getClient()),client);
		user.setClients(clients);
		dataStore.put(clientId.getUser(),user);
	}

	/**
	 * Call this method to commit to the DB
	 */
	void close()
	{
		dataStore.close();
	}
}
