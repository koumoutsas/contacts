package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.base.PublicKeysPlaintextSerializer;
import com.kareebo.contacts.common.Algorithm;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.InvalidArgument;
import org.apache.gora.store.DataStore;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ModifyKeys}
 */
public class ModifyKeysAsyncTest extends SignatureVerifierTestBase
{
	private final com.kareebo.contacts.common.PublicKeys newPublicKeys=new com.kareebo.contacts.common.PublicKeys();

	@Override
	PlaintextSerializer constructPlaintext()
	{
		return new PublicKeysPlaintextSerializer(newPublicKeys);
	}

	@Before
	@Override
	public void setUp() throws Exception
	{
		newPublicKeys.setEncryption(setUpCryptoBuffer("abc".getBytes()));
		newPublicKeys.setVerification(setUpCryptoBuffer("efg".getBytes()));
		super.setUp();
	}

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		return new ModifyKeys(dataStore);
	}

	@Test
	public void testModifyKeys1() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((ModifyKeys)signatureVerifier).modifyKeys1(newPublicKeys,signature,result);
		assertTrue(result.succeeded());
		assertEquals(TypeConverter.convert(newPublicKeys),clientValid.getKeys());
	}

	@Test
	public void testInvalidAlgorithm() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final com.kareebo.contacts.common.PublicKeys fakePublicKeys=new com.kareebo.contacts.common
			                                                                .PublicKeys();
		fakePublicKeys.setEncryption(newPublicKeys.getEncryption());
		fakePublicKeys.setVerification(newPublicKeys.getVerification());
		fakePublicKeys.getEncryption().setAlgorithm(Algorithm.Fake);
		((ModifyKeys)signatureVerifier).modifyKeys1(fakePublicKeys,null,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}
}