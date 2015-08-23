package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.gora.HashIdentity;
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

	void put(final ByteBuffer buffer,HashIdentity hashIdentity)
	{
		identityDatastore.put(buffer,hashIdentity);
	}
}