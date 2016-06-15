package com.kareebo.contacts.integrationTest.serverClient;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.client.persistentStorage.PersistentStorage;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TException;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Integration test for the server-client protocol for the ModifyUserAgent service
 */
public class ModifyUserAgentIntegrationTest extends SetterIntegrationTest<UserAgent>
{
	@Nonnull
	@Override
	protected UserAgent payload()
	{
		return new com.kareebo.contacts.thrift.UserAgent("a","b");
	}

	@Nonnull
	@Override
	protected Supplier<UserAgent> stateRetriever()
	{
		return ()->{
			try
			{
				return TypeConverter.convert(getClient().getUserAgent());
			}
			catch(TException|PersistentStorage.NoSuchKey e)
			{
				throw new RuntimeException(e.getMessage());
			}
		};
	}

	@Nonnull
	@Override
	com.kareebo.contacts.client.protocol.ServiceMethod serviceMethod()
	{
		return com.kareebo.contacts.client.protocol.ModifyUserAgent.method1;
	}

}
