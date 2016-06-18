package com.kareebo.contacts.client.persistentStorage;

import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TProtocol;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.*;

/**
 * Unit test for {@link ContextRetriever}
 */
public class ContextRetrieverTest
{
	@Test
	public void test() throws Exception
	{
		final PersistentStorageImplementation persistentStorageImplementation=new PersistentStorageImplementation();
		final ContextRetriever contextRetriever=new ContextRetriever(persistentStorageImplementation);
		final Context context1=ContextRetriever.create();
		Context context2=ContextRetriever.create();
		int numberOfRetries=0;
		while(context1.equals(context2))
		{
			if(++numberOfRetries>3)
			{
				fail("Improbable context conflict");
			}
			context2=ContextRetriever.create();
		}
		final LongId payload1=new LongId(1);
		final LongId payload2=new LongId(2);
		contextRetriever.put(context1,payload1);
		assertFalse(persistentStorageImplementation.inTransaction);
		contextRetriever.put(context2,payload2);
		assertFalse(persistentStorageImplementation.inTransaction);
		final LongId payloadRetrieved=new LongId();
		contextRetriever.get(payloadRetrieved,context1);
		assertEquals(payload1,payloadRetrieved);
		contextRetriever.get(payloadRetrieved,context2);
		assertEquals(payload2,payloadRetrieved);
		contextRetriever.remove(context1);
		assertFalse(persistentStorageImplementation.inTransaction);
		try
		{
			contextRetriever.remove(context1);
			fail();
		}
		catch(PersistentStorage.NoSuchKey e)
		{
			assertFalse(persistentStorageImplementation.inTransaction);
		}
		try
		{
			contextRetriever.put(context2,new TBase()
			{
				@Override
				public void read(final TProtocol tProtocol) throws TException
				{
				}

				@Override
				public void write(final TProtocol tProtocol) throws TException
				{
					throw new TException();
				}

				@Override
				public TFieldIdEnum fieldForId(final int i)
				{
					return null;
				}

				@Override
				public boolean isSet(final TFieldIdEnum tFieldIdEnum)
				{
					return false;
				}

				@Override
				public Object getFieldValue(final TFieldIdEnum tFieldIdEnum)
				{
					return null;
				}

				@Override
				public void setFieldValue(final TFieldIdEnum tFieldIdEnum,final Object o)
				{
				}

				@Override
				public TBase deepCopy()
				{
					return null;
				}

				@Override
				public void clear()
				{
				}

				@Override
				public int compareTo(final @Nonnull Object o)
				{
					return 0;
				}
			});
			fail();
		}
		catch(TException ignored)
		{
		}
	}
}