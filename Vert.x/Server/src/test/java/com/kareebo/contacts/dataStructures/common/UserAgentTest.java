package com.kareebo.contacts.dataStructures.common;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link com.kareebo.contacts.dataStructures.common.UserAgent}
 */
public class UserAgentTest
{
	private final String platform="A";
	private final String version="B";
	@Rule
	public ExpectedException thrown=ExpectedException.none();
	private UserAgent userAgent=new UserAgent(platform,version);

	@Test
	public void testGetPlatform() throws Exception
	{
		org.junit.Assert.assertEquals(platform,userAgent.getPlatform());
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Platform cannot be null");
		new UserAgent(null,version);
	}

	@Test
	public void testGetVersion() throws Exception
	{
		org.junit.Assert.assertEquals(version,userAgent.getVersion());
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Version cannot be null");
		new UserAgent(platform,null);
	}
}