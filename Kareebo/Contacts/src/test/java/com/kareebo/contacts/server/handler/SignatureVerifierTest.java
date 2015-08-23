package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.server.gora.UserAgent;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link SignatureVerifier}
 */
public class SignatureVerifierTest extends SignatureVerifierTestBase
{
	final static private UserAgent emptyUserAgent=new UserAgent();
	private static final String[] queryFields={"clients"};
	private final ClientId clientIdInvalid=new ClientId();
	private MemStore<Long,User> datastore;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		clientIdInvalid.setClient(clientIdValid.getClient()+1);
		clientIdInvalid.setUser(clientIdValid.getUser());
		emptyUserAgent.setPlatform("");
		emptyUserAgent.setVersion("");
	}

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		this.datastore=(MemStore<Long,User>)dataStore;
		return new SignatureVerifierMock(dataStore);
	}

	@Override
	PlaintextSerializer constructPlaintext()
	{
		final Vector<byte[]> ret=new Vector<>(2);
		ret.add("abc".getBytes());
		ret.add("cde".getBytes());
		return new TestPlaintextSerializer(ret);
	}

	@After
	public void tearDown() throws Exception
	{
		final User retrievedUser=datastore.get(clientIdValid.getUser(),queryFields);
		assertNotNull(retrievedUser);
		final Map<CharSequence,Client> clients=retrievedUser.getClients();
		assertEquals(1,clients.size());
		assertEquals(clientValid,clients.values().iterator().next());
	}

	@Test
	public void testVerify() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((SignatureVerifierMock)signatureVerifier).verify(plaintext,signature,result);
		assertTrue(result.succeeded());
		assertEquals(clientValid.getUserAgent(),emptyUserAgent);
		assertTrue(datastore.hasBeenClosed());
	}

	@Test
	public void testInvalidUser() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		signature.setClient(clientIdInvalid);
		try
		{
			((SignatureVerifierMock)signatureVerifier).verify(plaintext,signature,result);
		}
		catch(Exception e)
		{
			signature.setClient(clientIdValid);
			throw e;
		}
		signature.setClient(clientIdValid);
		testFailed(result);
	}

	private void testFailed(final Future<Void> result) throws Exception
	{
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(FailedOperation.class,result.cause().getClass());
		assertEquals(signatureVerifier.get(signature.getClient()).getUserAgent(),userAgent);
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testCorruptedSignature() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final ByteBuffer signatureBuffer=signature.bufferForBuffer();
		signatureBuffer.rewind();
		final byte[] falseSignatureBytes=new byte[signatureBuffer.remaining()];
		signatureBuffer.get(falseSignatureBytes);
		falseSignatureBytes[0]=(byte)(falseSignatureBytes[0]+1);
		final ByteBuffer falseSignatureBuffer=ByteBuffer.wrap(falseSignatureBytes);
		falseSignatureBuffer.mark();
		final SignatureBuffer falseSignature=new SignatureBuffer();
		falseSignature.setClient(clientIdValid);
		falseSignature.setBuffer(falseSignatureBuffer);
		((SignatureVerifierMock)signatureVerifier).verify(plaintext,falseSignature,result);
		testFailed(result);
	}

	@Test
	public void testInvalidAlgorithm() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final Client client=signatureVerifier.get(clientIdValid);
		final SignatureAlgorithm algorithm=client.getKeys().getVerification().getAlgorithm();
		client.getKeys().getVerification().setAlgorithm(SignatureAlgorithm.Fake);
		try
		{
			signatureVerifier.put(client);
			((SignatureVerifierMock)signatureVerifier).verify(plaintext,signature,result);
		}
		catch(Exception e)
		{
			client.getKeys().getVerification().setAlgorithm(algorithm);
			throw e;
		}
		client.getKeys().getVerification().setAlgorithm(algorithm);
		testFailed(result);
	}

	@Test
	public void testInvalidSignature() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((SignatureVerifierMock)signatureVerifier).verify(plaintext,wrongSignature,result);
		testFailed(result);
	}

	@Test
	public void testAfterVerificationThrows() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final SignatureVerifierMock signatureVerifierMock=(SignatureVerifierMock)signatureVerifier;
		signatureVerifierMock.shouldThrow=true;
		signatureVerifierMock.verify(plaintext,signature,result);
		testFailed(result);
	}

	private class SignatureVerifierMock extends SignatureVerifier
	{
		boolean shouldThrow=false;

		/**
		 * Constructor from a datastore
		 *
		 * @param dataStore The datastore
		 */
		SignatureVerifierMock(final DataStore<Long,User> dataStore)
		{
			super(dataStore);
		}

		void verify(final PlaintextSerializer plaintextSerializer,final SignatureBuffer signature,final Future<Void> future)
		{
			verify(plaintextSerializer,signature,future,new After()
			{
				@Override
				public void run(final User user,final Client client) throws FailedOperation
				{
					if(shouldThrow)
					{
						throw new FailedOperation();
					}
					client.setUserAgent(emptyUserAgent);
				}
			});
		}
	}
}
