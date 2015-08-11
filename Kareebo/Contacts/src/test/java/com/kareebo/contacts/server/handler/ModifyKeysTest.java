package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.base.PublicKeysPlaintextSerializer;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.PublicKeys;
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
public class ModifyKeysTest extends SignatureVerifierTestBase
{
	private final PublicKeys newPublicKeys=new PublicKeys();

	@Before
	@Override
	public void setUp() throws Exception
	{
		newPublicKeys.setEncryption(setUpEncryptionKey("abc".getBytes()));
		newPublicKeys.setVerification(setUpVerificationKey("efg".getBytes()));
		super.setUp();
	}

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		return new ModifyKeys(dataStore);
	}

	@Override
	PlaintextSerializer constructPlaintext()
	{
		return new PublicKeysPlaintextSerializer(newPublicKeys);
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
		final PublicKeys fakePublicKeys=new PublicKeys();
		fakePublicKeys.setEncryption(newPublicKeys.getEncryption());
		fakePublicKeys.setVerification(newPublicKeys.getVerification());
		fakePublicKeys.getEncryption().setAlgorithm(EncryptionAlgorithm.Fake);
		((ModifyKeys)signatureVerifier).modifyKeys1(fakePublicKeys,null,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(FailedOperation.class,result.cause().getClass());
	}
}