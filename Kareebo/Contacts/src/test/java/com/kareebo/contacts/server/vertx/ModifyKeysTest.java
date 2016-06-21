package com.kareebo.contacts.server.vertx;

import com.kareebo.contacts.server.gora.User;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ModifyKeys}
 */
public class ModifyKeysTest
{
	@Test
	public void create() throws Exception
	{
		//noinspection ConstantConditions
		assertTrue(new ModifyKeys(new com.kareebo.contacts.server.handler.Configuration(DataStoreFactory.getDataStore(Long.class,User
			                                                                                                                         .class,new Configuration()),null,null,null)).create() instanceof
			           com.kareebo.contacts.thrift.ModifyKeys.AsyncProcessor);
	}
}