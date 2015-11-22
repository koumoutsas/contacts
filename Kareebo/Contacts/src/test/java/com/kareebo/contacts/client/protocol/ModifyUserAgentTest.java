package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.PublicKeys;
import com.kareebo.contacts.thrift.ServiceMethod;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for {@link ModifyUserAgent}
 */
public class ModifyUserAgentTest
{
	@Test
	public void test() throws Exception
	{
		final List<SimpleTestHarness.TestBase> tests=new ArrayList<>(2);
		tests.add(new SimpleTestHarness.SimpleTestBase<UserAgent,Void>("userAgent")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return com.kareebo.contacts.base.service.ModifyUserAgent.method0;
			}

			@Override
			protected UserAgent constructPayload()
			{
				return new UserAgent("a","b");
			}

			@Override
			protected boolean isFinal()
			{
				return true;
			}
		});
		tests.add(new SimpleTestHarness.SimpleTestBase<PublicKeys,Void>("userAgent")
		{
			@Override
			protected ServiceMethod getServiceMethod()
			{
				return new ServiceMethod(com.kareebo.contacts.base.service.ModifyUserAgent
					                         .serviceName,"random");
			}

			@Override
			TBase constructPayload()
			{
				return null;
			}

			@Override
			protected boolean serviceNotFound()
			{
				return true;
			}
		});
		new SimpleTestHarness().test(tests);
	}
}