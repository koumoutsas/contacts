package com.kareebo.contacts.client.persistentStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link PersistentStorage} for tests
 */
public class PersistentStorageImplementation implements PersistentStorage
{
	final private Map<String,byte[]> storage=new HashMap<>();
	boolean inTransaction;

	@Override
	public byte[] get(final String key) throws NoSuchKey
	{
		if(!storage.containsKey(key))
		{
			throw new NoSuchKey();
		}
		return storage.get(key);
	}

	public void put(final String key,final byte[] value)
	{
		storage.put(key,value);
	}

	@Override
	public void remove(final String key) throws NoSuchKey
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
