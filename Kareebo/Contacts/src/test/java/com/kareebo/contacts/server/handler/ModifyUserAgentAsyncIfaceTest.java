package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.base.UserAgentPlaintextSerializer;
import com.kareebo.contacts.server.gora.User;
import org.apache.gora.store.DataStore;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ModifyUserAgentAsyncIface}
 */
public class ModifyUserAgentAsyncIfaceTest extends SignatureVerifierTestBase
{
	private final com.kareebo.contacts.common.UserAgent newUserAgent=new com.kareebo.contacts.common.UserAgent();

	@Override
	PlaintextSerializer constructPlaintext()
	{
		return new UserAgentPlaintextSerializer(newUserAgent);
	}

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
		return new ModifyUserAgentAsyncIface(dataStore);
	}

	@Test
	public void testModifyUserAgent1() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((ModifyUserAgentAsyncIface)signatureVerifier).modifyUserAgent1(newUserAgent,signature,result);
		assertTrue(result.succeeded());
		assertEquals(TypeConverter.convert(newUserAgent),clientValid.getUserAgent());
	}
}