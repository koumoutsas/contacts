package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link SignatureVerifier}
 */
public class SignatureVerifierTest extends SignatureVerifierTestBase
{
	private final ClientId clientIdInvalid=new ClientId();

	@Override
	PlaintextSerializer constructPlaintext()
	{
		final Vector<byte[]> ret=new Vector<>(2);
		ret.add("abc".getBytes());
		ret.add("cde".getBytes());
		return new TestPlaintextSerializer(ret);
	}

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		clientIdInvalid.setClient(clientIdValid.getClient()+1);
		clientIdInvalid.setUser(clientIdValid.getUser());
	}

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		return new SignatureVerifierMock(dataStore);
	}

	@Test
	public void testVerify() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		signatureVerifier.verify(plaintext,signature,result);
		assertTrue(result.succeeded());
		assertNull(clientValid.getUserAgent());
	}

	@Test
	public void testInvalidUser() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		signature.setClient(clientIdInvalid);
		try
		{
			signatureVerifier.verify(plaintext,signature,result);
		}
		catch(Exception e)
		{
			signature.setClient(clientIdValid);
			throw e;
		}
		signature.setClient(clientIdValid);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(FailedOperation.class,result.cause().getClass());
		assertNotNull(signatureVerifier.get(signature.getClient()).getUserAgent());
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
		signatureVerifier.verify(plaintext,falseSignature,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(FailedOperation.class,result.cause().getClass());
		assertNotNull(signatureVerifier.get(signature.getClient()).getUserAgent());
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
			signatureVerifier.verify(plaintext,signature,result);
		}
		catch(Exception e)
		{
			client.getKeys().getVerification().setAlgorithm(algorithm);
			throw e;
		}
		client.getKeys().getVerification().setAlgorithm(algorithm);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(FailedOperation.class,result.cause().getClass());
		assertNotNull(signatureVerifier.get(signature.getClient()).getUserAgent());
	}

	@Test
	public void testInvalidSignature() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		signatureVerifier.verify(plaintext,wrongSignature,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(FailedOperation.class,result.cause().getClass());
		assertNotNull(signatureVerifier.get(signature.getClient()).getUserAgent());
	}

	@Test
	public void testAfterVerificationThrows() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((SignatureVerifierMock)signatureVerifier).shouldThrow=true;
		signatureVerifier.verify(plaintext,signature,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(FailedOperation.class,result.cause().getClass());
		assertNotNull(signatureVerifier.get(signature.getClient()).getUserAgent());
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

		@Override
		void afterVerification(final User user,final Client client) throws FailedOperation
		{
			if(shouldThrow)
			{
				throw new FailedOperation();
			}
			client.setUserAgent(null);
		}
	}
}
