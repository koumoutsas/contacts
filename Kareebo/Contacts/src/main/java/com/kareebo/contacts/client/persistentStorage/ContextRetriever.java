package com.kareebo.contacts.client.persistentStorage;

import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobEnqueueingConstants;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.security.SecureRandom;

/**
 * Specialization of {@link PersistedObjectRetriever} for {@link Context}
 */
public class ContextRetriever
{
	private final PersistedObjectRetriever persistedObjectRetriever;

	/**
	 * Create a {@link PersistedObjectRetriever} from a {@link PersistentStorage}
	 *
	 * @param persistentStorage The {@link PersistentStorage}
	 */
	public ContextRetriever(final PersistentStorage persistentStorage)
	{
		persistedObjectRetriever=new PersistedObjectRetriever(persistentStorage);
	}

	/**
	 * Create a random context. There is a negligible probability that there is a conflict with an active context
	 *
	 * @return A randomly created context
	 */
	public static Context create()
	{
		return new Context(new SecureRandom().nextLong());
	}

	/// Wrapper around {@link PersistedObjectRetriever#get}
	public void get(final TBase object,final Context context) throws TException, PersistentStorage.NoSuchKey
	{
		persistedObjectRetriever.get(object,constructKey(context));
	}

	private static String constructKey(final Context context)
	{
		return JobEnqueueingConstants.StorageKey+'.'+context.getId();
	}

	/// Wrapper around {@link PersistedObjectRetriever#put}
	public void put(final Context context,final TBase value) throws TException
	{
		persistedObjectRetriever.start();
		try
		{
			persistedObjectRetriever.put(constructKey(context),value);
		}
		catch(TException e)
		{
			persistedObjectRetriever.rollback();
			throw e;
		}
		persistedObjectRetriever.commit();
	}

	/// Wrapper around {@link PersistedObjectRetriever#remove}
	public void remove(final Context context) throws PersistentStorage.NoSuchKey
	{
		persistedObjectRetriever.start();
		try
		{
			persistedObjectRetriever.remove(constructKey(context));
		}
		catch(PersistentStorage.NoSuchKey e)
		{
			persistedObjectRetriever.rollback();
			throw e;
		}
		persistedObjectRetriever.commit();
	}
}
