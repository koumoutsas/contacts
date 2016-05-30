package com.kareebo.contacts.client.persistentStorage;

import javax.annotation.Nonnull;

/// Interface to the client persistent storage
public interface PersistentStorage
{
	/**
	 * Get a byte buffer for a key
	 *
	 * @param key The storage key
	 * @return The byte buffer that is mapped to the key. Deserialization is performed by the caller of the method
	 * @throws NoSuchKey If the key cannot be found
	 */
	@Nonnull
	byte[] get(@Nonnull String key) throws NoSuchKey;

	/**
	 * Put a value to storage
	 *
	 * @param key   The key for the value
	 * @param value The value
	 */
	void put(final @Nonnull String key,@Nonnull byte[] value);

	/**
	 * Remove a key from storage
	 *
	 * @param key The key
	 * @throws NoSuchKey If the key cannot be found
	 */
	void remove(final @Nonnull String key) throws NoSuchKey;

	/// Start a transaction
	void start();

	/// Commit a transaction
	void commit();

	/// Rollback a transaction
	void rollback();

	class NoSuchKey extends Exception
	{
	}
}
