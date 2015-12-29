package com.kareebo.contacts.client.persistentStorage;

import com.kareebo.contacts.thrift.LongId;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Unit test for {@link PersistedObjectRetriever}
 */
public class PersistedObjectRetrieverTest
{
	final static private String key="key";
	final static private String wrongKey="x";
	final static private LongId expected=new LongId(9);
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testGet() throws Exception
	{
		final PersistentStorageImplementation persistentStorageImplementation=new PersistentStorageImplementation();
		persistentStorageImplementation.put(key,new TSerializer().serialize(expected));
		final PersistedObjectRetriever persistedObjectRetriever=new PersistedObjectRetriever(persistentStorageImplementation);
		final LongId retrieved=new LongId();
		persistedObjectRetriever.get(retrieved,key);
		assertEquals(expected,retrieved);
		thrown.expect(PersistentStorage.NoSuchKey.class);
		persistedObjectRetriever.get(retrieved,wrongKey);
	}

	@Test
	public void testPut() throws Exception
	{
		final PersistentStorageImplementation persistentStorageImplementation=new PersistentStorageImplementation();
		final PersistedObjectRetriever persistedObjectRetriever=new PersistedObjectRetriever(persistentStorageImplementation);
		persistedObjectRetriever.put(key,expected);
		final LongId retrieved=new LongId();
		new TDeserializer().deserialize(retrieved,persistentStorageImplementation.get(key));
		assertEquals(expected,retrieved);
	}

	@Test
	public void testRemove() throws Exception
	{
		final PersistentStorageImplementation persistentStorageImplementation=new PersistentStorageImplementation();
		final PersistedObjectRetriever persistedObjectRetriever=new PersistedObjectRetriever(persistentStorageImplementation);
		persistentStorageImplementation.put(key,new TSerializer().serialize(expected));
		persistedObjectRetriever.remove(key);
		final LongId retrieved=new LongId();
		try
		{
			persistedObjectRetriever.get(retrieved,key);
			fail();
		}
		catch(PersistentStorage.NoSuchKey ignored)
		{
		}
		thrown.expect(PersistentStorage.NoSuchKey.class);
		persistedObjectRetriever.remove(wrongKey);
	}

	@Test
	public void testTransaction() throws Exception
	{
		final PersistentStorageImplementation persistentStorageImplementation=new PersistentStorageImplementation();
		final PersistedObjectRetriever persistedObjectRetriever=new PersistedObjectRetriever(persistentStorageImplementation);
		persistedObjectRetriever.start();
		assertTrue(persistentStorageImplementation.inTransaction);
		persistedObjectRetriever.commit();
		assertFalse(persistentStorageImplementation.inTransaction);
		persistedObjectRetriever.start();
		assertTrue(persistentStorageImplementation.inTransaction);
		persistedObjectRetriever.rollback();
		assertFalse(persistentStorageImplementation.inTransaction);
	}
}