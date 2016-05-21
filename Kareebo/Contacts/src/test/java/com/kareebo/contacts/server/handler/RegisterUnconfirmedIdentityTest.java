package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.HashBufferSet;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TBase;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit test for {@link RegisterUnconfirmedIdentity}
 */
public class RegisterUnconfirmedIdentityTest
{
	@Test
	public void testRegisterUnconfirmedIdentity1() throws Exception
	{
		new Base()
		{
			void run() throws NoSuchAlgorithmException
			{
				final Future<Void> result=new DefaultFutureResult<>();
				((RegisterUnconfirmedIdentity)signatureVerifier).registerUnconfirmedIdentity1(new HashBufferSet(hashBufferSet),
					signature,result);
				assertTrue(result.succeeded());
				assertTrue(((MemStore)identityDataStore).hasBeenClosed());
				final HashSet<com.kareebo.contacts.server.gora.HashBuffer> newIdentities=new HashSet<>(hashBufferSet.size());
				for(final HashBuffer h : hashBufferSet)
				{
					newIdentities.add(TypeConverter.convert(h));
				}
				assertTrue(Utils.convertToSet(getUserValid().getIdentities()).containsAll(newIdentities));
			}
		}.run();
	}

	@Test
	public void testRegisterUnconfirmedIdentityIdentityExistsInIdentityDatastore() throws Exception
	{
		new BaseFailed()
		{
			@Override
			void setUpFailure()
			{
				final HashIdentity hashIdentity=new HashIdentity();
				b1.mark();
				hashIdentity.setHash(b1);
				final HashIdentityValue hashIdentityValue=new HashIdentityValue();
				hashIdentityValue.setConfirmers(new ArrayList<>());
				hashIdentityValue.setId((long)10);
				hashIdentity.setHashIdentity(hashIdentityValue);
				identityDataStore.put(b1,hashIdentity);
			}
		}.run();
	}

	@Test
	public void testRegisterUnconfirmedIdentityIdentityExists() throws Exception
	{
		new BaseFailed()
		{
			@Override
			void setUpFailure()
			{
				final com.kareebo.contacts.server.gora.HashBuffer hashBuffer=new com.kareebo.contacts.server.gora.HashBuffer();
				b1.mark();
				hashBuffer.setBuffer(b1);
				hashBuffer.setAlgorithm(com.kareebo.contacts.server.gora.HashAlgorithm.SHA256);
				getUserValid().getIdentities().add(hashBuffer);
			}
		}.run();
	}

	@Test
	public void testRegisterUnconfirmedIdentityInvalidAlgorithm() throws Exception
	{
		new BaseFailed()
		{
			@Override
			void setUpDatastores() throws Exception
			{
				hashBufferSet.add(new HashBuffer(b1,HashAlgorithm.Fake));
				super.setUpDatastores();
			}

			@Override
			void setUpFailure()
			{
			}
		}.run();
	}

	private abstract class Base extends SignatureVerifierTestBase
	{
		final Set<HashBuffer> hashBufferSet=new HashSet<>();
		final ByteBuffer b1=ByteBuffer.wrap("1".getBytes());
		DataStore<ByteBuffer,HashIdentity> identityDataStore;

		Base() throws Exception
		{
			setUpDatastores();
		}

		void setUpDatastores() throws Exception
		{
			identityDataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
			final ByteBuffer b2=ByteBuffer.wrap("2".getBytes());
			hashBufferSet.add(new HashBuffer(b1,HashAlgorithm.SHA256));
			hashBufferSet.add(new HashBuffer(b2,HashAlgorithm.SHA256));
			super.setUp();
		}

		@Override
		SignatureVerifier construct(final DataStore<Long,User> dataStore)
		{
			return new RegisterUnconfirmedIdentity(dataStore,identityDataStore);
		}

		@Override
		TBase constructPlaintext()
		{
			return new HashBufferSet(hashBufferSet);
		}
	}

	abstract private class BaseFailed extends Base
	{
		BaseFailed() throws Exception
		{
		}

		void run()
		{
			setUpFailure();
			final Set<com.kareebo.contacts.server.gora.HashBuffer> original=Utils.convertToSet(getUserValid().getIdentities());
			final Future<Void> result=new DefaultFutureResult<>();
			((RegisterUnconfirmedIdentity)signatureVerifier).registerUnconfirmedIdentity1(new HashBufferSet(hashBufferSet),signature,
				result);
			assertTrue(result.failed());
			//noinspection ThrowableResultOfMethodCallIgnored
			assertEquals(FailedOperation.class,result.cause().getClass());
			assertFalse(((MemStore)identityDataStore).hasBeenClosed());
			assertEquals(original,Utils.convertToSet(getUserValid().getIdentities()));
		}

		abstract void setUpFailure();
	}
}