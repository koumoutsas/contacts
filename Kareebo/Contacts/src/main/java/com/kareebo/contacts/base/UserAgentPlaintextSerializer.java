package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.UserAgent;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link UserAgent}
 */
public class UserAgentPlaintextSerializer implements PlaintextSerializer
{
	final private UserAgent userAgent;

	public UserAgentPlaintextSerializer(final UserAgent userAgent)
	{
		this.userAgent=userAgent;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(2);
		ret.add(userAgent.getPlatform().getBytes());
		ret.add(userAgent.getVersion().getBytes());
		return ret;
	}
}
