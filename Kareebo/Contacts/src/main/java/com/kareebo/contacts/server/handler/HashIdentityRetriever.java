package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import org.apache.gora.store.DataStore;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;

/**
 * Retrieves {@link HashIdentityValue} from a {@link DataStore} following aliases
 */
class HashIdentityRetriever
{
	final DataStore<ByteBuffer,HashIdentity> dataStore;

	public HashIdentityRetriever(final DataStore<ByteBuffer,HashIdentity> dataStore)
	{
		this.dataStore=dataStore;
	}

	/**
	 * Find the identity mapping to a key, resolving all intermediate aliases
	 *
	 * @param key The key to look for
	 * @return The resolved value, null if there is no mapped value
	 * @throws IllegalStateException When a corrupted datastore is detected
	 */
	Long find(final ByteBuffer key)
	{
		final HashSet<ByteBuffer> seenKeys=new HashSet<>();
		for(ByteBuffer nextKey=key;;)
		{
			if(!seenKeys.add(nextKey))
			{
				throw new IllegalStateException("Cycle detected for key "+new String(nextKey.array(),Charset.forName("UTF-8")));
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
				return ((HashIdentityValue)value).getId();
			}
			if(value instanceof ByteBuffer)
			{
				nextKey=(ByteBuffer)value;
			}
			else
			{
				throw new IllegalStateException("Unknown value type "+value.getClass().toString()+"for key "+nextKey.toString());
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
		for(final ByteBuffer key : aliases)
		{
			final HashIdentity identity=new HashIdentity();
			identity.setHash(key);
			identity.setHashIdentity(primary);
			dataStore.put(key,identity);
		}
		dataStore.close();
	}
}
