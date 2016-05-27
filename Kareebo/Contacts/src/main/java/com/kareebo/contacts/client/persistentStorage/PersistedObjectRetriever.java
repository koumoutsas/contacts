package com.kareebo.contacts.client.persistentStorage;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import javax.annotation.Nonnull;

/// Wrapper around {@link PersistentStorage} that deserializes to {@link TBase}
public class PersistedObjectRetriever
{
	final private PersistentStorage persistentStorage;

	/**
	 * Create a {@link PersistedObjectRetriever} from a {@link PersistentStorage}
	 *
	 * @param persistentStorage The {@link PersistentStorage}
	 */
	public PersistedObjectRetriever(final @Nonnull PersistentStorage persistentStorage)
	{
		this.persistentStorage=persistentStorage;
	}

	/**
	 * Get a {@link TBase} object by key
	 *
	 * @param object The retrieved object
	 * @param key    The key
	 * @throws PersistentStorage.NoSuchKey When {@param key} cannot be found
	 * @throws TException                  When deserialization to {@param object}fails
	 */
	public void get(final @Nonnull TBase object,final @Nonnull String key) throws PersistentStorage.NoSuchKey, TException
	{
		new TDeserializer().deserialize(object,persistentStorage.get(key));
	}

	/**
	 * Put a {@link TBase} object in storage
	 *
	 * @param key   The key
	 * @param value The object
	 * @throws TException When serialization of {@param value} fails
	 */
	public void put(final @Nonnull String key,final @Nonnull TBase value) throws TException
	{
		persistentStorage.put(key,new TSerializer().serialize(value));
	}

	/**
	 * Remove a key from storage
	 *
	 * @param key The key
	 * @throws PersistentStorage.NoSuchKey When {@param key} cannot be found
	 */
	void remove(final @Nonnull String key) throws PersistentStorage.NoSuchKey
	{
		persistentStorage.remove(key);
	}

	/// Start a transaction
	void start()
	{
		persistentStorage.start();
	}

	/// Commit a transaction
	void commit()
	{
		persistentStorage.commit();
	}

	/// Rollback a transaction
	void rollback()
	{
		persistentStorage.rollback();
	}
}
