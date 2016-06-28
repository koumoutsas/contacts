package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.crypto.TestEncryptionKeyPair;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.PublicKeys;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ModifyKeys}
 */
public class ModifyKeysTest
{
	@Test
	public void testModifyKeys1() throws Exception
	{
		new Base()
		{
			@Override
			PublicKeys createKeys() throws NoSuchAlgorithmException, NoSuchProviderException
			{
				return new PublicKeys(TypeConverter.convert(new TestEncryptionKeyPair().getEncryptionKey()),TypeConverter.convert(verificationKey));
			}

			void run() throws NoSuchAlgorithmException
			{
				final Future<Void> result=new DefaultFutureResult<>();
				((ModifyKeys)signatureVerifier).modifyKeys1(newPublicKeys,signature,result);
				assertTrue(result.succeeded());
				assertEquals(TypeConverter.convert(newPublicKeys),clientValid.getKeys());
			}
		}.run();
	}

	@Test
	public void testInvalidAlgorithm() throws Exception
	{
		new Base()
		{
			void run()
			{
				final Future<Void> result=new DefaultFutureResult<>();
				((ModifyKeys)signatureVerifier).modifyKeys1(newPublicKeys,signature,result);
				assertTrue(result.failed());
				//noinspection ThrowableResultOfMethodCallIgnored
				assertEquals(FailedOperation.class,result.cause().getClass());
			}

			@Override
			PublicKeys createKeys() throws NoSuchAlgorithmException, NoSuchProviderException
			{
				final EncryptionKey encryptionKey=TypeConverter.convert(new TestEncryptionKeyPair().getEncryptionKey());
				encryptionKey.setAlgorithm(EncryptionAlgorithm.Fake);
				return new PublicKeys(encryptionKey,TypeConverter.convert(verificationKey));
			}
		}.run();
	}

	abstract private class Base extends SignatureVerifierTestBase
	{
		PublicKeys newPublicKeys;

		Base() throws Exception
		{
			setUp();
		}

		public void setUp() throws Exception
		{
			newPublicKeys=createKeys();
			super.setUp();
		}

		abstract PublicKeys createKeys() throws NoSuchAlgorithmException, NoSuchProviderException;

		@Override
		SignatureVerifier construct(final DataStore<Long,User> dataStore)
		{
			//noinspection ConstantConditions
			return new ModifyKeys(new com.kareebo.contacts.server.handler.Configuration(dataStore,null,null,null));
		}

		@Override
		TBase constructPlaintext()
		{
			return newPublicKeys;
		}
	}
}