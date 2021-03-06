package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.server.gora.UserAgent;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.util.Map;

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
	TBase constructPlaintext()
	{
		return new LongId(9);
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
		((SignatureVerifierMock)signatureVerifier).verify(constructPlaintext(),signature,result);
		assertTrue(result.succeeded());
		assertEquals(clientValid.getUserAgent(),emptyUserAgent);
		assertTrue(datastore.hasBeenClosed());
	}

	@Test
	public void testInvalidUser() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		signature.setClient(clientIdInvalid);
		((SignatureVerifierMock)signatureVerifier).verify(constructPlaintext(),signature,result);
		signature.setClient(clientIdValid);
		testFailed(result);
	}

	private void testFailed(final Future<Void> result) throws Exception
	{
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(FailedOperation.class,result.cause().getClass());
		assertEquals(signatureVerifier.clientDBAccessor.get(signature.getClient()).getUserAgent(),userAgent);
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testInvalidSerialization() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		signature.setClient(clientIdValid);
		((SignatureVerifierMock)signatureVerifier).verify(new LongId()
		{
			{
				setId(((LongId)constructPlaintext()).getId());
			}

			@Override
			public void write(TProtocol var1) throws TException
			{
				throw new TException();
			}
		},signature,result);
		signature.setClient(clientIdValid);
		testFailed(result);
	}

	@Test
	public void testCorruptedSignature() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final byte[] falseSignatureBytes=Utils.getBytes(signature.bufferForBuffer());
		falseSignatureBytes[0]=(byte)(falseSignatureBytes[0]+1);
		final ByteBuffer falseSignatureBuffer=ByteBuffer.wrap(falseSignatureBytes);
		falseSignatureBuffer.mark();
		final SignatureBuffer falseSignature=new SignatureBuffer();
		falseSignature.setClient(clientIdValid);
		falseSignature.setBuffer(falseSignatureBuffer);
		((SignatureVerifierMock)signatureVerifier).verify(constructPlaintext(),falseSignature,result);
		testFailed(result);
	}

	@Test
	public void testInvalidAlgorithm() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final Client client=signatureVerifier.clientDBAccessor.get(clientIdValid);
		final SignatureAlgorithm algorithm=client.getKeys().getVerification().getAlgorithm();
		client.getKeys().getVerification().setAlgorithm(SignatureAlgorithm.Fake);
		signatureVerifier.clientDBAccessor.put(client);
		((SignatureVerifierMock)signatureVerifier).verify(constructPlaintext(),signature,result);
		client.getKeys().getVerification().setAlgorithm(algorithm);
		testFailed(result);
	}

	@Test
	public void testInvalidSignature() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((SignatureVerifierMock)signatureVerifier).verify(constructPlaintext(),wrongSignature,result);
		testFailed(result);
	}

	@Test
	public void testAfterVerificationThrows() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final SignatureVerifierMock signatureVerifierMock=(SignatureVerifierMock)signatureVerifier;
		signatureVerifierMock.shouldThrow=true;
		signatureVerifierMock.verify(constructPlaintext(),signature,result);
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

		void verify(final TBase plaintext,final SignatureBuffer signature,final Future<Void> future)
		{
			verify(plaintext,signature,new Reply<>(future),(user,client)->{
				if(shouldThrow)
				{
					throw new FailedOperation();
				}
				client.setUserAgent(emptyUserAgent);
			});
		}
	}
}
