package com.kareebo.contacts.base;

import java.util.Vector;

/**
 * Plaintext serializer for strings
 */
public class StringPlaintextSerializer implements PlaintextSerializer
{
	final private String s;

	public StringPlaintextSerializer(final String s)
	{
		this.s=s;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(1);
		ret.add(s.getBytes());
		return ret;
	}
}
