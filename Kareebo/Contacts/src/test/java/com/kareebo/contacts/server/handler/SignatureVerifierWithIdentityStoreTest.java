package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TBase;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit test for {@link SignatureVerifierWithIdentityStore}
 */
public class SignatureVerifierWithIdentityStoreTest extends SignatureVerifierTestBase
{
	private final ClientId clientIdInvalid=new ClientId();
	private DataStore<ByteBuffer,HashIdentity> identityDatastore;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		clientIdInvalid.setClient(clientIdValid.getClient()+1);
		clientIdInvalid.setUser(clientIdValid.getUser());
	}

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		try
		{
			identityDatastore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
		}
		catch(GoraException e)
		{
			fail("Unable to initialize identity datastore");
		}
		return new SignatureVerifierMock(dataStore,identityDatastore);
	}

	@Override
	TBase constructPlaintext()
	{
		return new LongId(9);
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
		value.setConfirmers(new ArrayList<Long>());
		identity.setHashIdentity(value);
		identityDatastore.put(key,identity);
		assertEquals(value.getId(),((SignatureVerifierWithIdentityStore)signatureVerifier).find(key));
	}

	@Test(expected=FailedOperation.class)
	public void testFindFailed() throws Exception
	{
		final ByteBuffer key=ByteBuffer.wrap("abc".getBytes());
		key.mark();
		((SignatureVerifierWithIdentityStore)signatureVerifier).find(key);
	}

	@Test
	public void testExists() throws Exception
	{
		final HashIdentity identity=new HashIdentity();
		final ByteBuffer key=ByteBuffer.wrap("abc".getBytes());
		key.mark();
		identity.setHash(key);
		final HashIdentityValue value=new HashIdentityValue();
		value.setId((long)0);
		value.setConfirmers(new ArrayList<Long>());
		identity.setHashIdentity(value);
		identityDatastore.put(key,identity);
		assertTrue(((SignatureVerifierWithIdentityStore)signatureVerifier).exists(key));
		final ByteBuffer keyFalse=ByteBuffer.wrap("def".getBytes());
		keyFalse.mark();
		assertFalse(((SignatureVerifierWithIdentityStore)signatureVerifier).exists(keyFalse));
	}

	@Test
	public void testVerify() throws Exception
	{
		((SignatureVerifierMock)signatureVerifier).verify(plaintext,signature,new DefaultFutureResult<Void>());
		assertTrue(((MemStore)identityDatastore).hasBeenClosed());
	}

	@Test
	public void testVerifyFailed() throws Exception
	{
		signature.setClient(clientIdInvalid);
		((SignatureVerifierMock)signatureVerifier).verify(plaintext,signature,new DefaultFutureResult<Void>());
		assertFalse(((MemStore)identityDatastore).hasBeenClosed());
	}

	@Test
	public void testPut() throws Exception
	{
		final ByteBuffer key=ByteBuffer.wrap("abc".getBytes());
		key.mark();
		final HashIdentityValue identity=new HashIdentityValue();
		identity.setId((long)0);
		identity.setConfirmers(new ArrayList<Long>());
		((SignatureVerifierWithIdentityStore)signatureVerifier).put(key,identity);
		assertEquals(identity,identityDatastore.get(key).getHashIdentity());
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
		value.setConfirmers(new ArrayList<Long>());
		identity.setHashIdentity(value);
		identityDatastore.put(key,identity);
		assertEquals(value,((SignatureVerifierWithIdentityStore)signatureVerifier).get(key));
	}

	@Test
	public void testAliasTo() throws Exception
	{
		final ByteBuffer key=ByteBuffer.wrap("abc".getBytes());
		key.mark();
		final ByteBuffer alias=ByteBuffer.wrap("def".getBytes());
		alias.mark();
		((SignatureVerifierWithIdentityStore)signatureVerifier).aliasTo(key,alias);
		final HashIdentity retrieved=identityDatastore.get(key);
		assertNotNull(retrieved);
		assertEquals(key,retrieved.getHash());
		assertTrue(retrieved.getHashIdentity() instanceof ByteBuffer);
		assertEquals(alias,retrieved.getHashIdentity());
	}

	private class SignatureVerifierMock extends SignatureVerifierWithIdentityStore
	{
		/**
		 * Constructor from a datastore
		 *
		 * @param dataStore The datastore
		 */
		SignatureVerifierMock(final DataStore<Long,User> dataStore,final DataStore<ByteBuffer,HashIdentity>
			                                                           identityDatastore)
		{
			super(dataStore,identityDatastore);
		}

		void verify(final TBase plaintext,final SignatureBuffer signature,final Future<Void> future)
		{
			verify(plaintext,signature,new Reply<>(future),new After()
			{
				@Override
				public void run(final User user,final Client client) throws FailedOperation
				{
				}
			});
		}
	}
}