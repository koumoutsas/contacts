package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;

/**
 * Extension of {@link SignatureVerifier} with an identity {@link DataStore}
 */
abstract class SignatureVerifierWithIdentityStore extends SignatureVerifier
{
	final private HashIdentityRetriever hashIdentityRetriever;
	final private DataStore<ByteBuffer,HashIdentity>
		identityDatastore;

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	SignatureVerifierWithIdentityStore(final DataStore<Long,User> dataStore,final DataStore<ByteBuffer,HashIdentity>
		                                                                        identityDatastore)
	{
		super(dataStore);
		this.identityDatastore=identityDatastore;
		hashIdentityRetriever=new HashIdentityRetriever(identityDatastore);
	}

	/**
	 * Find the identity mapping to a key, resolving all intermediate aliases
	 *
	 * @param key The key to look for
	 * @return The resolved value, null if there is no mapped value
	 * @throws IllegalStateException When a corrupted datastore is detected
	 */
	final Long find(final ByteBuffer key)
	{
		return hashIdentityRetriever.find(key);
	}

	@Override
	void verify(final PlaintextSerializer plaintextSerializer,final SignatureBuffer signature,final Future<?> future,final After after)
	{
		super.verify(plaintextSerializer,signature,future,after);
		if(future.succeeded())
		{
			identityDatastore.close();
		}
	}

	/**
	 * Add an identity to the datastore
	 *
	 * @param key          The identity key
	 * @param hashIdentity The identity
	 */
	void put(final ByteBuffer key,final HashIdentityValue hashIdentity)
	{
		final HashIdentity identity=new HashIdentity();
		identity.setHash(key);
		identity.setHashIdentity(hashIdentity);
		identityDatastore.put(key,identity);
	}

	/**
	 * Create an alias
	 *
	 * @param from The value to be aliased
	 * @param to   The alias
	 */
	void aliasTo(final ByteBuffer from,final ByteBuffer to)
	{
		final HashIdentity alias=new HashIdentity();
		alias.setHash(from);
		alias.setHashIdentity(to);
		identityDatastore.put(from,alias);
	}

	/**
	 * Get the identity for a key
	 *
	 * @param key The key
	 * @return The identity mapped to the key, null if there isn't one
	 * @throws IllegalStateException When a corrupted datastore is detected
	 */
	HashIdentityValue get(final ByteBuffer key)
	{
		return hashIdentityRetriever.get(key);
	}
}