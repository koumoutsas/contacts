package com.kareebo.contacts.base.vertx;

import org.junit.Test;
import org.vertx.java.platform.Container;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ServiceStarter}
 */
public class ServiceStarterTest
{
	@Test
	public void test() throws Throwable
	{
		final List<ServiceStarter.ConfigurationSection> registry=new ArrayList<>();
		final Container container=new Utils.Container("{\"services\":[{\"name\":\"service0\",\"port\":0,\"address\":\"localhost\"},"+
			                                              "{\"name\":\"service1\",\"port\":1,\"address\":\"localhost\"}]}");
		new ServiceStarter(container,configurationSection->{
			registry.add(configurationSection);
			return null;
		});
		assertEquals(2,registry.size());
		registry.forEach(configurationSection->{
			assertEquals("localhost",configurationSection.address);
			assertEquals("service"+configurationSection.port,configurationSection.service);
		});
	}

	@Test
	public void testError() throws Exception
	{
		final Container container=new Utils.Container("{\"services\":[{\"name\":\"service0\",\"port\":0,\"address\":\"localhost\"}]}");
		final Throwable error=new Throwable();
		Throwable thrown=null;
		try
		{
			new ServiceStarter(container,configurationSection->error);
		}
		catch(Throwable throwable)
		{
			thrown=throwable;
		}
		assertEquals(error,thrown);
	}
}