package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.IdPair;
import com.kareebo.contacts.thrift.InvalidArgument;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link ClientDBAccessor}
 */
public class ClientDBAccessorTest
{
	final long userIdValid=0;
	private final IdPair idPairNew=new IdPair();
	private final IdPair idPairPreset=new IdPair();
	private final IdPair idPairInvalidClient=new IdPair();
	private final IdPair idPairInvalidUser=new IdPair();
	private final Client clientNew=new Client();
	private final Client clientPreset=new Client();
	private final byte[] bufferBytes={'a','b'};
	@Rule
	public ExpectedException exception=ExpectedException.none();
	private DataStore<Long,User> dataStore;
	private ClientDBAccessor clientDBAccessor;

	@Before
	public void setUp() throws Exception
	{
		long clientId=0;
		idPairNew.setClientId(clientId++);
		idPairNew.setUserId(userIdValid);
		idPairPreset.setClientId(clientId++);
		idPairPreset.setUserId(userIdValid);
		idPairInvalidClient.setClientId(clientId++);
		idPairInvalidClient.setUserId(userIdValid);
		idPairInvalidUser.setClientId(clientId);
		idPairInvalidUser.setUserId(userIdValid+1);
		final UserAgent userAgent=new UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		final PublicKeys publicKeys=new PublicKeys();
		final CryptoBuffer cryptoBuffer=new CryptoBuffer();
		cryptoBuffer.setAlgorithm(Algorithm.SHA256withECDSAprime239v1);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bufferBytes);
		byteBuffer.mark();
		cryptoBuffer.setBuffer(byteBuffer);
		publicKeys.setEncryption(cryptoBuffer);
		publicKeys.setVerification(cryptoBuffer);
		clientPreset.setUserAgent(userAgent);
		clientPreset.setKeys(publicKeys);
		clientPreset.setRegistered(false);
		clientPreset.setContacts(new ArrayList<HashedContact>());
		dataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		final User user=new User();
		final Handle handle=new Handle();
		handle.setType(HandleType.EmailAddress);
		handle.setContents("a");
		user.setHandle(handle);
		final HashMap<CharSequence,Client> clients=new HashMap<>();
		clients.put(TypeConverter.convert(idPairPreset.getClientId()),clientPreset);
		user.setClients(clients);
		dataStore.put(userIdValid,user);
		clientDBAccessor=new ClientDBAccessor(dataStore);
	}

	@After
	public void tearDown() throws Exception
	{
		clientDBAccessor.close();
		assertNull(dataStore.get(userIdValid));
	}

	@Test
	public void testGet() throws Exception
	{
		final Client client=clientDBAccessor.get(idPairPreset);
		assertNotNull(client);
		assertEquals(clientPreset,client);
		final ByteBuffer byteBuffer=clientPreset.getKeys().getEncryption().getBuffer();
		byteBuffer.rewind();
		assertEquals(bufferBytes.length,byteBuffer.remaining());
		final byte[] duplicateBytes=new byte[bufferBytes.length];
		byteBuffer.get(duplicateBytes);
		assertArrayEquals(bufferBytes,duplicateBytes);
	}

	@Test
	public void testGetInvalidUser() throws Exception
	{
		exception.expect(InvalidArgument.class);
		clientDBAccessor.get(idPairInvalidUser);
	}

	@Test
	public void testGetInvalidClient() throws Exception
	{
		exception.expect(InvalidArgument.class);
		clientDBAccessor.get(idPairInvalidClient);
	}

	@Test
	public void testPut() throws Exception
	{
		clientDBAccessor.get(idPairPreset);
		clientDBAccessor.put(clientPreset);
		final User user=dataStore.get(idPairPreset.getUserId());
		assertNotNull(user);
		assertTrue(user.getClients().containsKey(TypeConverter.convert(idPairPreset.getClientId())));
	}

	@Test
	public void testPutInvalidState() throws Exception
	{
		try
		{
			clientDBAccessor.get(idPairInvalidUser);
			assertTrue(false);
		}
		catch(InvalidArgument e)
		{
			assertTrue(true);
		}
		exception.expect(IllegalStateException.class);
		clientDBAccessor.put(clientNew);
	}

	@Test
	public void testPut2() throws Exception
	{
		clientDBAccessor.put(idPairNew,clientNew);
		final User user=dataStore.get(idPairNew.getUserId());
		assertNotNull(user);
		assertTrue(user.getClients().containsKey(TypeConverter.convert(idPairNew.getClientId())));
	}

	@Test
	public void testPut2InvalidUser() throws Exception
	{
		exception.expect(InvalidArgument.class);
		clientDBAccessor.put(idPairInvalidUser,clientNew);
	}
}
