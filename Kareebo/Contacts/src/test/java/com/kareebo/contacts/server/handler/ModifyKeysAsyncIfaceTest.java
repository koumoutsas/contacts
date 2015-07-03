package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.common.Algorithm;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.InvalidArgument;
import org.apache.gora.store.DataStore;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ModifyKeysAsyncIface}
 */
public class ModifyKeysAsyncIfaceTest extends SignatureVerifierTestBase
{
	private final com.kareebo.contacts.common.PublicKeys newPublicKeys=new com.kareebo.contacts.common.PublicKeys();

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
		newPublicKeys.setEncryption(setUpCryptoBuffer(plaintext.elementAt(0)));
		newPublicKeys.setVerification(setUpCryptoBuffer(plaintext.elementAt(1)));
	}

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		return new ModifyKeysAsyncIface(dataStore);
	}

	@Test
	public void testModifyKeys1() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((ModifyKeysAsyncIface)signatureVerifier).modifyKeys1(newPublicKeys,signature,result);
		assertTrue(result.succeeded());
		assertEquals(TypeConverter.convert(newPublicKeys),clientValid.getKeys());
	}

	@Test
	public void testInvalidAlgorithm() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final com.kareebo.contacts.common.PublicKeys fakePublicKeys=new com.kareebo.contacts.common
			                                                                .PublicKeys();
		fakePublicKeys.setEncryption(setUpCryptoBuffer(plaintext.elementAt(0)));
		fakePublicKeys.setVerification(setUpCryptoBuffer(plaintext.elementAt(1)));
		fakePublicKeys.getEncryption().setAlgorithm(Algorithm.Fake);
		((ModifyKeysAsyncIface)signatureVerifier).modifyKeys1(fakePublicKeys,null,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}
}