package com.kareebo.contacts.integrationTest.serverClient;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.client.protocol.ModifyUserAgent;
import com.kareebo.contacts.client.protocol.ServiceMethod;
import com.kareebo.contacts.thrift.UserAgent;

import javax.annotation.Nonnull;

/**
 * Integration test for the server-client protocol for the ModifyUserAgent service
 */
public class ModifyUserAgentIntegrationTest extends SetterIntegrationTest<UserAgent>
{
	@Nonnull
	@Override
	protected UserAgent payload()
	{
		return new UserAgent("a","b");
	}

	@Nonnull
	@Override
	ServiceMethod serviceMethod()
	{
		return ModifyUserAgent.method1;
	}

	@Nonnull
	@Override
	protected ThrowingSupplier<UserAgent> stateRetrieverImplementation()
	{
		return ()->TypeConverter.convert(getClient().getUserAgent());
	}

	@Override
	protected boolean isIdempotent()
	{
		return true;
	}
}
