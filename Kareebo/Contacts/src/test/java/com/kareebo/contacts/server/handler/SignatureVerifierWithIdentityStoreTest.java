package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Vector;

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
	PlaintextSerializer constructPlaintext()
	{
		final Vector<byte[]> ret=new Vector<>(2);
		ret.add("abc".getBytes());
		ret.add("cde".getBytes());
		return new TestPlaintextSerializer(ret);
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
		final HashIdentity identity=new HashIdentity();
		final ByteBuffer key=ByteBuffer.wrap("abc".getBytes());
		key.mark();
		identity.setHash(key);
		final HashIdentityValue value=new HashIdentityValue();
		value.setId((long)0);
		value.setConfirmers(new ArrayList<Long>());
		identity.setHashIdentity(value);
		((SignatureVerifierWithIdentityStore)signatureVerifier).put(key,identity);
		assertEquals(identity,identityDatastore.get(key));
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

		void verify(final PlaintextSerializer plaintextSerializer,final SignatureBuffer signature,final Future<Void> future)
		{
			verify(plaintextSerializer,signature,future,new After()
			{
				@Override
				public void run(final User user,final Client client) throws FailedOperation
				{
				}
			});
		}
	}
}