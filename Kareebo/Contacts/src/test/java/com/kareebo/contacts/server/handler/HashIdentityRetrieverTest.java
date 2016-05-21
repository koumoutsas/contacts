package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit test for {@link HashIdentityRetriever}
 */
public class HashIdentityRetrieverTest
{
	private DataStore<ByteBuffer,HashIdentity> dataStore;
	private HashIdentityRetriever retriever;

	@Before
	public void setUp() throws Exception
	{
		dataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
		retriever=new HashIdentityRetriever(dataStore);
	}

	@Test
	public void testFind() throws Exception
	{
		final HashIdentity identity=new HashIdentity();
		final ByteBuffer key=ByteBuffer.wrap("abc".getBytes());
		key.mark();
		identity.setHash(key);
		final HashIdentityValue value=new HashIdentityValue();
		value.setId((long)0);
		value.setConfirmers(new ArrayList<>());
		identity.setHashIdentity(value);
		dataStore.put(key,identity);
		final ByteBuffer alias=createAlias("def",key);
		final ByteBuffer doubleAlias=createAlias("ghi",alias);
		final ByteBuffer tripleAlias=createAlias("jkl",doubleAlias);
		assertNull(retriever.find(ByteBuffer.wrap("".getBytes())));
		final Long expected=value.getId();
		assertEquals(expected,retriever.find(key));
		assertEquals(expected,retriever.find(alias));
		assertEquals(expected,retriever.find(doubleAlias));
		assertEquals(expected,retriever.find(tripleAlias));
		final Object retrieved=dataStore.get(tripleAlias).getHashIdentity();
		assertEquals(key,retrieved);
		assertTrue(((MemStore)dataStore).hasBeenClosed());
	}

	private ByteBuffer createAlias(final String keyString,final ByteBuffer key)
	{
		final ByteBuffer alias=ByteBuffer.wrap(keyString.getBytes());
		alias.mark();
		final HashIdentity aliased=new HashIdentity();
		aliased.setHash(alias);
		aliased.setHashIdentity(key);
		dataStore.put(alias,aliased);
		return alias;
	}

	@Test(expected=FailedOperation.class)
	public void testWrongType() throws Exception
	{
		final ByteBuffer wrongKey=ByteBuffer.wrap("123".getBytes());
		wrongKey.mark();
		final HashIdentity identity=new HashIdentity();
		identity.setHash(wrongKey);
		identity.setHashIdentity("456");
		dataStore.put(wrongKey,identity);
		retriever.find(wrongKey);
	}

	@Test(expected=FailedOperation.class)
	public void testCycle0() throws Exception
	{
		final String aString="123";
		final ByteBuffer aKey=ByteBuffer.wrap(aString.getBytes());
		aKey.mark();
		createAlias(aString,aKey);
		retriever.find(aKey);
	}

	@Test(expected=FailedOperation.class)
	public void testCycle1() throws Exception
	{
		final String aString="123";
		final String bString="456";
		final ByteBuffer bKey=ByteBuffer.wrap(bString.getBytes());
		bKey.mark();
		final ByteBuffer aKey=createAlias(bString,createAlias(aString,bKey));
		retriever.find(aKey);
	}

	@Test(expected=FailedOperation.class)
	public void testCycle2() throws Exception
	{
		final String aString="123";
		final String bString="456";
		final String cString="789";
		final ByteBuffer bKey=ByteBuffer.wrap(bString.getBytes());
		bKey.mark();
		final ByteBuffer cKey=ByteBuffer.wrap(cString.getBytes());
		cKey.mark();
		final ByteBuffer aKey=createAlias(aString,bKey);
		createAlias(bString,cKey);
		createAlias(cString,aKey);
		retriever.find(aKey);
	}

	@Test
	public void testGet() throws Exception
	{
		final HashIdentity identity=new HashIdentity();
		final ByteBuffer key=ByteBuffer.wrap("abc".getBytes());
		key.mark();
		identity.setHash(key);
		final HashIdentityValue value=new HashIdentityValue();
		value.setId((long)0);
		value.setConfirmers(new ArrayList<>());
		identity.setHashIdentity(value);
		dataStore.put(key,identity);
		final ByteBuffer alias=createAlias("def",key);
		final ByteBuffer doubleAlias=createAlias("ghi",alias);
		final ByteBuffer tripleAlias=createAlias("jkl",doubleAlias);
		assertNull(retriever.get(ByteBuffer.wrap("".getBytes())));
		assertEquals(value,retriever.get(key));
		assertEquals(value,retriever.get(alias));
		assertEquals(value,retriever.get(doubleAlias));
		assertEquals(value,retriever.get(tripleAlias));
		final Object retrieved=dataStore.get(tripleAlias).getHashIdentity();
		assertEquals(key,retrieved);
		assertTrue(((MemStore)dataStore).hasBeenClosed());
	}
}