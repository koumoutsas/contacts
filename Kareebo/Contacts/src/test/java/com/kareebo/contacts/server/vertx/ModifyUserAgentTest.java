package com.kareebo.contacts.server.vertx;

import com.kareebo.contacts.server.gora.User;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ModifyUserAgent}
 */
public class ModifyUserAgentTest
{
	@Test
	public void create() throws Exception
	{
		assertTrue(new ModifyUserAgent(DataStoreFactory.getDataStore(Long.class,User.class,new Configuration())).create() instanceof com.kareebo.contacts.thrift.ModifyUserAgent.AsyncProcessor);
	}
}