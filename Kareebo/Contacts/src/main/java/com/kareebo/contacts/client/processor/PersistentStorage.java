package com.kareebo.contacts.client.processor;

/// Interface to the client persistent storage
public interface PersistentStorage
{
	/// Definitions of keys - TODO Move this to thrift
	public static final String PrivateKeys="PrivateKeys";

	/**
	 * Get a byte buffer for a key
	 * @param key The storage key
	 * @return The bute buffer that is mapped to the key. Deserialization is performed by the caller of the method
	 * @throws NoSuchKey If the key cannot be found
	*/
	byte[] get(String key) throws NoSuchKey;
	
	static public NoSuchKey extends Exception
	{
	}
}
