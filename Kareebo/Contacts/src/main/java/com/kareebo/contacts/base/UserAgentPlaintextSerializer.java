package com.kareebo.contacts.base;

import com.kareebo.contacts.common.UserAgent;

import java.util.Vector;

/**
 * Plaintext serializer for user agent
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
