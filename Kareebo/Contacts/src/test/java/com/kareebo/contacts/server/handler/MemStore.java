package com.kareebo.contacts.server.handler;

import org.apache.gora.persistency.impl.PersistentBase;

/**
 * Extension of {@link org.apache.gora.memory.store.MemStore} that doesn't clear its contents at close
 */
public class MemStore<K, T extends PersistentBase> extends org.apache.gora.memory.store.MemStore<K, T>
{
	private boolean isClosed=false;

	public boolean hasBeenClosed()
	{
		return isClosed;
	}

	@Override
	public void close()
	{
		isClosed=true;
	}
}
