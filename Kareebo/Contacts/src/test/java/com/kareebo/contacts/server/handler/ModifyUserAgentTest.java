package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.BasePlaintextSerializer;
import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.gora.store.DataStore;
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
		return new ModifyUserAgent(dataStore);
	}

	@Override
	PlaintextSerializer constructPlaintext()
	{
		return new BasePlaintextSerializer<>(newUserAgent);
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