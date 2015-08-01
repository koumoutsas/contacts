package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ClientDBAccessor}
 */
public class ClientDBAccessorTest
{
	final long userIdValid=0;
	private final ClientId clientIdNew=new ClientId();
	private final ClientId clientIdPreset=new ClientId();
	private final ClientId clientIdInvalidClient=new ClientId();
	private final ClientId clientIdInvalidUser=new ClientId();
	private final Client clientNew=new Client();
	private final Client clientPreset=new Client();
	private final byte[] bufferBytes={'a','b'};
	private DataStore<Long,User> dataStore;
	private ClientDBAccessor clientDBAccessor;

	@Before
	public void setUp() throws Exception
	{
		long clientId=0;
		clientIdNew.setClient(clientId++);
		clientIdNew.setUser(userIdValid);
		clientIdPreset.setClient(clientId++);
		clientIdPreset.setUser(userIdValid);
		clientIdInvalidClient.setClient(clientId++);
		clientIdInvalidClient.setUser(userIdValid);
		clientIdInvalidUser.setClient(clientId);
		clientIdInvalidUser.setUser(userIdValid+1);
		final UserAgent userAgent=new UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		final PublicKeys publicKeys=new PublicKeys();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bufferBytes);
		byteBuffer.mark();
		final EncryptionKey encryptionKey=new EncryptionKey();
		encryptionKey.setAlgorithm(EncryptionAlgorithm.RSA2048);
		encryptionKey.setBuffer(byteBuffer);
		publicKeys.setEncryption(encryptionKey);
		final VerificationKey verificationKey=new VerificationKey();
		verificationKey.setAlgorithm(SignatureAlgorithm.SHA256withECDSAprime239v1);
		verificationKey.setBuffer(byteBuffer);
		publicKeys.setVerification(verificationKey);
		clientPreset.setUserAgent(userAgent);
		clientPreset.setKeys(publicKeys);
		clientPreset.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
		dataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		final User user=new User();
		user.setBlind(byteBuffer);
		final HashMap<CharSequence,Client> clients=new HashMap<>();
		clients.put(TypeConverter.convert(clientIdPreset.getClient()),clientPreset);
		user.setClients(clients);
		user.setIdentities(new ArrayList<HashBuffer>());
		user.setSentRequests(new ArrayList<HashBuffer>());
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
		final Client client=clientDBAccessor.get(clientIdPreset);
		assertNotNull(client);
		assertEquals(clientPreset,client);
		final ByteBuffer byteBuffer=clientPreset.getKeys().getEncryption().getBuffer();
		byteBuffer.rewind();
		assertEquals(bufferBytes.length,byteBuffer.remaining());
		final byte[] duplicateBytes=new byte[bufferBytes.length];
		byteBuffer.get(duplicateBytes);
		assertArrayEquals(bufferBytes,duplicateBytes);
	}

	@Test(expected=FailedOperation.class)
	public void testGetInvalidUser() throws Exception
	{
		clientDBAccessor.get(clientIdInvalidUser);
	}

	@Test(expected=FailedOperation.class)
	public void testGetInvalidClient() throws Exception
	{
		clientDBAccessor.get(clientIdInvalidClient);
	}

	@Test
	public void testPut() throws Exception
	{
		clientDBAccessor.get(clientIdPreset);
		clientDBAccessor.put(clientPreset);
		final User user=dataStore.get(clientIdPreset.getUser());
		assertNotNull(user);
		assertTrue(user.getClients().containsKey(TypeConverter.convert(clientIdPreset.getClient())));
	}

	@Test(expected=IllegalStateException.class)
	public void testPutInvalidState() throws Exception
	{
		try
		{
			clientDBAccessor.get(clientIdInvalidUser);
			assertTrue(false);
		}
		catch(FailedOperation e)
		{
			assertTrue(true);
		}
		clientDBAccessor.put(clientNew);
	}

	@Test
	public void testPut2() throws Exception
	{
		clientDBAccessor.put(clientIdNew,clientNew);
		final User user=dataStore.get(clientIdNew.getUser());
		assertNotNull(user);
		assertTrue(user.getClients().containsKey(TypeConverter.convert(clientIdNew.getClient())));
	}

	@Test(expected=FailedOperation.class)
	public void testPut2InvalidUser() throws Exception
	{
		clientDBAccessor.put(clientIdInvalidUser,clientNew);
	}
}