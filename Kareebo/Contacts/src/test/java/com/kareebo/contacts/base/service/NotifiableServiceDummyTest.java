package com.kareebo.contacts.base.service;

import org.junit.Test;

/**
 * This test is only for achieving 100% test coverage for the {@link com.kareebo.contacts.thrift.client.jobs.ServiceMethod} providers
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