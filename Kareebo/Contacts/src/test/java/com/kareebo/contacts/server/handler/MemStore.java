package com.kareebo.contacts.server.handler;

import org.apache.gora.persistency.impl.PersistentBase;

/**
 * Extension of {@link org.apache.gora.memory.store.MemStore} that doesn't clear its contents at close
 */
public class MemStore<K,T extends PersistentBase> extends org.apache.gora.memory.store.MemStore<K,T>
{
	public K useId;
	private boolean isClosed=false;

	public boolean hasBeenClosed()
	{
		return isClosed;
	}

	@Override
	public T get(K var1,String[] var2)
	{
		return super.get(useId==null?var1:useId,var2);
	}

	@Override
	public void close()
	{
		isClosed=true;
	}
}
