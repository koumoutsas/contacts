package com.kareebo.contacts.client.vertx;

import com.kareebo.contacts.client.jobs.*;
import com.kareebo.contacts.client.persistentStorage.PersistentStorage;
import com.kareebo.contacts.crypto.TestSignatureKeyPair;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.client.persistentStorage.PersistentStorageConstants;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Collection of implementation classes that are injected for tests
 */
class Implementations
{
	static class TestIntermediateResultEnqueuer implements IntermediateResultEnqueuer
	{
		@Override
		public void put(@Nonnull final IntermediateJob job)
		{
		}
	}

	static class TestFinalResultEnqueuer implements FinalResultEnqueuer
	{
		@Override
		public void success(@Nonnull final SuccessJob job)
		{
		}

		@Override
		public void error(@Nonnull final ErrorJob job)
		{
		}
	}

	static class TestPersistentStorage implements PersistentStorage
	{
		@Nonnull
		@Override
		public byte[] get(@Nonnull final String key) throws NoSuchKey
		{
			switch(key)
			{
				case PersistentStorageConstants.SigningKey:
					try
					{
						return getSigningKey();
					}
					catch(InvalidAlgorithmParameterException|NoSuchAlgorithmException|InvalidKeySpecException|NoSuchProviderException|TException e)
					{
						return new byte[0];
					}
				case PersistentStorageConstants.ClientId:
					try
					{
						return new TSerializer().serialize(new ClientId(0,0));
					}
					catch(TException e)
					{
						return new byte[0];
					}
				default:
					throw new NoSuchKey();
			}
		}

		@Nonnull
		private byte[] getSigningKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, TException
		{
			final ByteBuffer buffer=ByteBuffer.wrap(new PKCS8EncodedKeySpec(new TestSignatureKeyPair().getPrivate().getEncoded()).getEncoded());
			buffer.mark();
			return new TSerializer().serialize(new com.kareebo.contacts.thrift.client.SigningKey(buffer,SignatureAlgorithm.Fake));
		}

		@Override
		public void put(@Nonnull final String key,@Nonnull final byte[] value)
		{
		}

		@Override
		public void remove(@Nonnull final String key) throws NoSuchKey
		{
		}

		@Override
		public void start()
		{
		}

		@Override
		public void commit()
		{
		}

		@Override
		public void rollback()
		{
		}
	}
}