package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all handlers that operate at the client level
 */
class ClientDBAccessor
{
	private static final Logger logger=LoggerFactory.getLogger(ClientDBAccessor.class.getName());
	/**
	 * Restricts the query fields to the clients only
	 */
	private static final String[] queryFields={"clients"};
	/**
	 * The datastore is initialized in the constructor and is never null
	 */
	final private DataStore<Long,User> dataStore;
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
	ClientDBAccessor(final @Nonnull DataStore<Long,User> dataStore)
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
	@Nonnull
	Client get(final @Nonnull ClientId clientId) throws FailedOperation
	{
		getClients(clientId);
		final Client client=clients.get(TypeConverter.convert(clientId.getClient()));
		if(client==null)
		{
			resetState();
			logger.error("No client for "+clientId);
			throw new FailedOperation();
		}
		return client;
	}

	private void getClients(final ClientId clientId) throws FailedOperation
	{
		this.clientId=clientId;
		get(clientId.getUser());
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
	 * @param client   The client
	 * @throws FailedOperation When the user cannot be found in the DB
	 */
	void put(final @Nonnull ClientId clientId,final @Nonnull Client client) throws FailedOperation
	{
		getClients(clientId);
		put(client);
	}

	/**
	 * Set a client for a user that has been retrieved before with get. The client can exist already, in
	 * which case it's an update, or not, in which case it's an insert
	 *
	 * @param client The client
	 * @throws FailedOperation When the state is not correct
	 */
	void put(final @Nonnull Client client) throws FailedOperation
	{
		if(user==null||clients==null||clientId==null)
		{
			throw new FailedOperation();
		}
		clients.put(TypeConverter.convert(clientId.getClient()),client);
		user.setClients(clients);
		put(user);
	}

	/**
	 * Store a user in the datastore
	 *
	 * @param user The user
	 */
	void put(final @Nonnull User user)
	{
		dataStore.put(user.getId(),user);
	}

	/**
	 * Create and commit a new user
	 *
	 * @return The user that was created
	 */
	@Nonnull
	User createNewUser() throws FailedOperation
	{
		final Long id=new SecureRandom().nextLong();
		boolean exists=false;
		try
		{
			get(id);
			exists=true;
		}
		catch(FailedOperation ignored)
		{
		}
		if(exists)
		{
			logger.error("Random id "+id+" already exists");
			throw new FailedOperation();
		}
		final User user=new User();
		final ByteBuffer blind=ByteBuffer.wrap("".getBytes());
		blind.mark();
		user.setBlind(blind);
		user.setClients(new HashMap<>());
		user.setId(id);
		user.setIdentities(new ArrayList<>());
		user.setSentRequests(new ArrayList<>());
		dataStore.put(id,user);
		return user;
	}

	/**
	 * Call this method to commit to the DB
	 */
	void close()
	{
		dataStore.close();
	}

	/**
	 * Get a user by id
	 *
	 * @param id The user id
	 * @return The user
	 * @throws FailedOperation If there is no user for the id
	 */
	@Nonnull
	User get(final @Nonnull Long id) throws FailedOperation
	{
		user=dataStore.get(id,queryFields);
		if(user==null)
		{
			logger.error("No user for "+id);
			resetState();
			throw new FailedOperation();
		}
		return user;
	}

	/**
	 * Create and commit a new client for a user that was retrieved
	 *
	 * @return The id of the new client that was created
	 * @throws FailedOperation When the state is not correct or an id couldn't be generated
	 */
	@Nonnull
	Long createNewClient() throws FailedOperation
	{
		if(user==null)
		{
			throw new FailedOperation();
		}
		clients=user.getClients();
		final Long id=new SecureRandom().nextLong();
		final CharSequence key=TypeConverter.convert(id);
		if(clients.containsKey(key))
		{
			logger.error("Random id "+id+" already exists");
			throw new FailedOperation();
		}
		final Client client=new Client();
		client.setComparisonIdentities(new ArrayList<>());
		final PublicKeys publicKeys=new PublicKeys();
		final EncryptionKey encryptionKey=new EncryptionKey();
		encryptionKey.setAlgorithm(EncryptionAlgorithm.Fake);
		final ByteBuffer b=ByteBuffer.wrap("".getBytes());
		b.mark();
		encryptionKey.setBuffer(b);
		publicKeys.setEncryption(encryptionKey);
		final VerificationKey verificationKey=new VerificationKey();
		verificationKey.setAlgorithm(SignatureAlgorithm.Fake);
		verificationKey.setBuffer(b);
		publicKeys.setVerification(verificationKey);
		client.setKeys(publicKeys);
		final UserAgent userAgent=new UserAgent();
		userAgent.setPlatform("");
		userAgent.setVersion("");
		client.setUserAgent(userAgent);
		clients.put(key,client);
		user.setClients(clients);
		dataStore.put(user.getId(),user);
		return id;
	}
}