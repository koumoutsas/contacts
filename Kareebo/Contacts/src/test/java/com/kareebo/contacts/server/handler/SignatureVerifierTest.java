package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.Algorithm;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.IdPair;
import com.kareebo.contacts.thrift.InvalidArgument;
import org.apache.gora.store.DataStore;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link SignatureVerifier}
 */
public class SignatureVerifierTest extends SignatureVerifierTestBase
{
	private final IdPair idPairInvalid=new IdPair();

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		return new SignatureVerifierMock(dataStore);
	}

	@Override
	Vector<byte[]> constructPlaintext()
	{
		final Vector<byte[]> ret=new Vector<>(2);
		ret.add("abc".getBytes());
		ret.add("cde".getBytes());
		return ret;
	}

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		idPairInvalid.setClientId(idPairValid.getClientId()+1);
		idPairInvalid.setUserId(idPairValid.getUserId());
	}

	@Test
	public void testVerify() throws Exception
	{
		final boolean registered=signatureVerifier.get(signature.getIds()).getRegistered();
		final Future<Void> result=new DefaultFutureResult<>();
		signatureVerifier.verify(plaintext,signature,result);
		assertTrue(result.succeeded());
		assertEquals(!registered,clientValid.getRegistered());
	}

	@Test
	public void testInvalidUser() throws Exception
	{
		final boolean registered=signatureVerifier.get(signature.getIds()).getRegistered();
		final Future<Void> result=new DefaultFutureResult<>();
		signature.setIds(idPairInvalid);
		try
		{
			signatureVerifier.verify(plaintext,signature,result);
		}
		catch(Exception e)
		{
			signature.setIds(idPairValid);
			throw e;
		}
		signature.setIds(idPairValid);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(InvalidArgument.class,result.cause().getClass());
		assertEquals(registered,signatureVerifier.get(signature.getIds()).getRegistered());
	}

	@Test
	public void testCorruptedSignature() throws Exception
	{
		final boolean registered=signatureVerifier.get(signature.getIds()).getRegistered();
		final Future<Void> result=new DefaultFutureResult<>();
		final ByteBuffer signatureBuffer=signature.bufferForSignature();
		signatureBuffer.rewind();
		final byte[] falseSignatureBytes=new byte[signatureBuffer.remaining()];
		signatureBuffer.get(falseSignatureBytes);
		falseSignatureBytes[0]=(byte)(falseSignatureBytes[0]+1);
		final ByteBuffer falseSignatureBuffer=ByteBuffer.wrap(falseSignatureBytes);
		falseSignatureBuffer.mark();
		final com.kareebo.contacts.thrift.Signature falseSignature=new com.kareebo.contacts.thrift.Signature();
		falseSignature.setIds(idPairValid);
		falseSignature.setSignature(falseSignatureBuffer);
		signatureVerifier.verify(plaintext,falseSignature,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(InvalidArgument.class,result.cause().getClass());
		assertEquals(registered,signatureVerifier.get(signature.getIds()).getRegistered());
	}

	@Test
	public void testInvalidAlgorithm() throws Exception
	{
		final boolean registered=signatureVerifier.get(signature.getIds()).getRegistered();
		final Future<Void> result=new DefaultFutureResult<>();
		final Client client=signatureVerifier.get(idPairValid);
		final Algorithm algorithm=client.getKeys().getVerification().getAlgorithm();
		client.getKeys().getVerification().setAlgorithm(Algorithm.SHA256);
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
		assertEquals(InvalidArgument.class,result.cause().getClass());
		assertEquals(registered,signatureVerifier.get(signature.getIds()).getRegistered());
	}

	@Test
	public void testInvalidSignature() throws Exception
	{
		final boolean registered=signatureVerifier.get(signature.getIds()).getRegistered();
		final Future<Void> result=new DefaultFutureResult<>();
		signatureVerifier.verify(plaintext,wrongSignature,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(InvalidArgument.class,result.cause().getClass());
		assertEquals(registered,signatureVerifier.get(signature.getIds()).getRegistered());
	}

	private class SignatureVerifierMock extends SignatureVerifier
	{
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
		void afterVerification(final Client client)
		{
			client.setRegistered(!client.getRegistered());
		}
	}
}