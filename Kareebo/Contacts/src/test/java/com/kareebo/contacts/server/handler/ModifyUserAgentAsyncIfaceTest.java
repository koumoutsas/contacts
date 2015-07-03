package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.User;
import org.apache.gora.store.DataStore;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ModifyUserAgentAsyncIface}
 */
public class ModifyUserAgentAsyncIfaceTest extends SignatureVerifierTestBase
{
	private final com.kareebo.contacts.common.UserAgent newUserAgent=new com.kareebo.contacts.common.UserAgent();
	private final String platform="platform";
	private final String version="version";

	@Override
	Vector<byte[]> constructPlaintext()
	{
		final Vector<byte[]> ret=new Vector<>(2);
		ret.add(platform.getBytes());
		ret.add(version.getBytes());
		return ret;
	}

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		newUserAgent.setPlatform(platform);
		newUserAgent.setVersion(version);
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