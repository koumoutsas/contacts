package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.UserAgent;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
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
				return com.kareebo.contacts.client.protocol.ModifyUserAgent.method1;
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
		new SimpleTestHarness().test(tests);
	}
}