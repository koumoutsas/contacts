package com.kareebo.contacts.client.processor;

/// Wrapper around {@link PersistentStorage} that deserialzies to {@link TBase}
class PersistedObjectRetriever
{
	final private PersistentStorage persistentStorage;

	PersistedObjectRetriever(final PersistentStorage persistentStorage)
	{
		this.persistentStorage=persistentStorage;
	}
	
	TBase get(final String key) throws PersistentStorage.NoSuchKey
	{
		final TBase ret=new TBase();
		new TDeserializer().deserialize(ret,persistentStorage.get(key));
		return ret;
	}
}
