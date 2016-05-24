package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;

/**
 * Retrieves {@link HashIdentityValue} from a {@link DataStore} following aliases
 */
class HashIdentityRetriever
{
	private static final Logger logger=LoggerFactory.getLogger(HashIdentityRetriever.class.getName());
	final private DataStore<ByteBuffer,HashIdentity> dataStore;

	HashIdentityRetriever(final DataStore<ByteBuffer,HashIdentity> dataStore)
	{
		this.dataStore=dataStore;
	}

	/**
	 * Find the user id mapping to a key, resolving all intermediate aliases
	 *
	 * @param key The key to look for
	 * @return The resolved user id, null if there is no mapped value
	 * @throws FailedOperation When a corrupted datastore is detected
	 */
	Long find(final ByteBuffer key) throws FailedOperation
	{
		final HashIdentityValue value=get(key);
		if(value==null)
		{
			return null;
		}
		return value.getId();
	}

	/**
	 * Get the identity mapping to a key, resolving all intermediate aliases
	 *
	 * @param key The key to look for
	 * @return The resolved value, null if there is no mapped value
	 * @throws FailedOperation When a corrupted datastore is detected
	 */
	HashIdentityValue get(final ByteBuffer key) throws FailedOperation
	{
		final HashSet<ByteBuffer> seenKeys=new HashSet<>();
		for(ByteBuffer nextKey=key;;)
		{
			if(!seenKeys.add(nextKey))
			{
				logger.error("Cycle detected for key "+new String(nextKey.array(),Charset.forName("UTF-8")));
				throw new FailedOperation();
			}
			final HashIdentity hashIdentity=dataStore.get(nextKey);
			if(hashIdentity==null)
			{
				return null;
			}
			final Object value=hashIdentity.getHashIdentity();
			if(value instanceof HashIdentityValue)
			{
				promoteAliases(seenKeys,nextKey);
				return (HashIdentityValue)value;
			}
			if(value instanceof ByteBuffer)
			{
				nextKey=(ByteBuffer)value;
			}
			else
			{
				logger.error("Unknown value type "+value.getClass().toString()+"for key "+nextKey.toString());
				throw new FailedOperation();
			}
		}
	}

	/**
	 * All aliases that are two or more hops from the real value are promoted to point to the primary alias. This speeds up future lookups
	 *
	 * @param aliases The set of aliases, including the primary alias
	 * @param primary The primary alias
	 */
	private void promoteAliases(final HashSet<ByteBuffer> aliases,final ByteBuffer primary)
	{
		if(aliases.size()<3)
		{
			return;
		}
		aliases.remove(primary);
		aliases.stream().forEach(key->{
			final HashIdentity identity=new HashIdentity();
			identity.setHash(key);
			identity.setHashIdentity(primary);
			dataStore.put(key,identity);
		});
		dataStore.close();
	}
}
