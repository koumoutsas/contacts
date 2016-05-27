package com.kareebo.contacts.client.persistentStorage;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link PersistentStorage} for tests
 */
public class PersistentStorageImplementation implements PersistentStorage
{
	final private Map<String,byte[]> storage=new HashMap<>();
	boolean inTransaction;

	@Nonnull
	@Override
	public byte[] get(@Nonnull final String key) throws NoSuchKey
	{
		if(!storage.containsKey(key))
		{
			throw new NoSuchKey();
		}
		return storage.get(key);
	}

	public void put(@Nonnull final String key,@Nonnull final byte[] value)
	{
		storage.put(key,value);
	}

	@Override
	public void remove(@Nonnull final String key) throws NoSuchKey
	{
		if(!storage.containsKey(key))
		{
			throw new NoSuchKey();
		}
		storage.remove(key);
	}

	@Override
	public void start()
	{
		inTransaction=true;
	}

	@Override
	public void commit()
	{
		inTransaction=false;
	}

	@Override
	public void rollback()
	{
		inTransaction=false;
	}
}
