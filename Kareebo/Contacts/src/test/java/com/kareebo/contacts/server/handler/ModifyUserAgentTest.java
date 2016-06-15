package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ModifyUserAgent}
 */
public class ModifyUserAgentTest extends SignatureVerifierTestBase
{
	private final UserAgent newUserAgent=new UserAgent();

	@Before
	@Override
	public void setUp() throws Exception
	{
		newUserAgent.setPlatform("platform");
		newUserAgent.setVersion("version");
		super.setUp();
	}

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		//noinspection ConstantConditions
		return new ModifyUserAgent(new Configuration(dataStore,null,null,null));
	}

	@Override
	TBase constructPlaintext()
	{
		return newUserAgent;
	}

	@Test
	public void testModifyUserAgent1() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((ModifyUserAgent)signatureVerifier).modifyUserAgent1(newUserAgent,signature,result);
		assertTrue(result.succeeded());
		assertEquals(TypeConverter.convert(newUserAgent),clientValid.getUserAgent());
	}
}