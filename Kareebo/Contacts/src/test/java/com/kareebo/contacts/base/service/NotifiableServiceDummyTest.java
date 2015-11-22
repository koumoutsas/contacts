package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.ServiceMethod;
import org.junit.Test;

/**
 * This test is only for achieving 100% test coverage for the {@link ServiceMethod} providers
 */
public class NotifiableServiceDummyTest
{
	@Test
	public void test()
	{
		new BroadcastNewContactIdentity();
		new SendContactCard();
		new SuggestNewContact();
		new ModifyKeys();
		new ModifyUserAgent();
		new RegisterIdentity();
		new RegisterUnconfirmedIdentity();
		new UpdateServerContactBook();
	}
}