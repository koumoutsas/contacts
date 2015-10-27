package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
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
	final User userValid=new User();
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
		userValid.setBlind(byteBuffer);
		final HashMap<CharSequence,Client> clients=new HashMap<>();
		clients.put(TypeConverter.convert(clientIdPreset.getClient()),clientPreset);
		userValid.setClients(clients);
		userValid.setIdentities(new ArrayList<HashBuffer>());
		userValid.setSentRequests(new ArrayList<HashBuffer>());
		dataStore.put(userIdValid,userValid);
		clientDBAccessor=new ClientDBAccessor(dataStore);
	}

	@Test
	public void testGet() throws Exception
	{
		final Client client=clientDBAccessor.get(clientIdPreset);
		assertNotNull(client);
		assertEquals(clientPreset,client);
		final byte[] duplicateBytes=Utils.getBytes(clientPreset.getKeys().getEncryption().getBuffer());
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

	@Test(expected=FailedOperation.class)
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

	@Test
	public void testPutUser() throws Exception
	{
		clientDBAccessor.put(userValid);
		final User user=dataStore.get(userValid.getId());
		assertEquals(userValid,user);
	}

	@Test(expected=FailedOperation.class)
	public void testPut2InvalidUser() throws Exception
	{
		clientDBAccessor.put(clientIdInvalidUser,clientNew);
	}

	@Test
	public void testGetUser() throws Exception
	{
		final User user=clientDBAccessor.get(clientIdPreset.getUser());
		assertNotNull(user);
		assertEquals(userValid,user);
	}

	@Test(expected=FailedOperation.class)
	public void testGetUserInvalid() throws Exception
	{
		clientDBAccessor.get(clientIdInvalidUser.getUser());
	}

	@Test
	public void testCreateNewUser() throws Exception
	{
		final User user=clientDBAccessor.createNewUser();
		assertEquals(user,dataStore.get(user.getId()));
	}

	@Test(expected=FailedOperation.class)
	public void testCreateNewUserFailed() throws Exception
	{
		((MemStore<Long,User>)dataStore).useId=userIdValid;
		clientDBAccessor.createNewUser();
	}

	@Test
	public void testCreateNewClient() throws Exception
	{
		final User user=clientDBAccessor.get(userIdValid);
		assertTrue(user.getClients().containsKey(TypeConverter.convert(clientDBAccessor.createNewClient())));
	}

	@Test(expected=FailedOperation.class)
	public void testCreateNewClientFailed1() throws Exception
	{
		clientDBAccessor.createNewClient();
	}

	@Test(expected=FailedOperation.class)
	public void testCreateNewClientFailed2() throws Exception
	{
		final User user=clientDBAccessor.get(userIdValid);
		class MyMap extends HashMap<CharSequence,Client>
		{
			@Override
			public boolean containsKey(Object O)
			{
				return true;
			}
		}
		user.setClients(new MyMap());
		clientDBAccessor.createNewClient();
	}

	@Test
	public void testClose() throws Exception
	{
		assertFalse(((MemStore)dataStore).hasBeenClosed());
		clientDBAccessor.close();
		assertTrue(((MemStore)dataStore).hasBeenClosed());
	}
}