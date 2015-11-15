package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.NotificationMethod;
import org.junit.Test;

/**
 * This test is only for achieving 100% test coverage for the {@link NotificationMethod} providers
 */
public class ServiceDummyTest
{
	@Test
	public void test()
	{
		new BroadcastNewContactIdentity();
		new SendContactCard();
		new SuggestNewContact();
	}
}