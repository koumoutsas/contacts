package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * Extension of {@link SignatureVerifier} with an identity {@link DataStore}
 */
abstract class SignatureVerifierWithIdentityStore extends SignatureVerifier
{
	final private HashIdentityRetriever hashIdentityRetriever;
	final private DataStore<ByteBuffer,HashIdentity> identityDatastore;

	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The user datastore
	 * @param identityDatastore The identity datastore
	 */
	SignatureVerifierWithIdentityStore(final @Nonnull DataStore<Long,User> userDataStore,final @Nonnull DataStore<ByteBuffer,HashIdentity> identityDatastore)
	{
		super(userDataStore);
		this.identityDatastore=identityDatastore;
		hashIdentityRetriever=new HashIdentityRetriever(identityDatastore);
	}

	/**
	 * Find the identity mapping to a key, resolving all intermediate aliases
	 *
	 * @param key The key to look for
	 * @return The resolved value, null if there is no mapped value
	 * @throws FailedOperation When the datastore is corrupted or the key is not found
	 */
	final
	@Nonnull
	Long find(final @Nonnull ByteBuffer key) throws FailedOperation
	{
		final Long ret=hashIdentityRetriever.find(key);
		if(ret==null)
		{
			throw new FailedOperation();
		}
		return ret;
	}

	/**
	 * Check if a key exists
	 *
	 * @param key The key
	 * @return True, iff the key exists
	 */
	boolean exists(final @Nonnull ByteBuffer key) throws FailedOperation
	{
		return hashIdentityRetriever.find(key)!=null;
	}

	@Override
	void verify(@Nonnull final TBase plaintext,@Nonnull final SignatureBuffer signature,@Nonnull final Reply<?> reply,@Nonnull final After after)
	{
		super.verify(plaintext,signature,reply,after);
		if(!reply.failed())
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
	void put(final @Nonnull ByteBuffer key,final @Nonnull HashIdentityValue hashIdentity)
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
	void aliasTo(final @Nonnull ByteBuffer from,final @Nonnull ByteBuffer to)
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
	 * @throws FailedOperation When the datastore is corrupted or the key is not found
	 */
	HashIdentityValue get(final @Nonnull ByteBuffer key) throws FailedOperation
	{
		return hashIdentityRetriever.get(key);
	}
}