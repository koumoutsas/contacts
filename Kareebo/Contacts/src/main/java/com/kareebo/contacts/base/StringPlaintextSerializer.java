package com.kareebo.contacts.base;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link String}
 */
public class StringPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=1;
	final private String s;

	public StringPlaintextSerializer(final String s)
	{
		this.s=s;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		ret.add(s.getBytes());
		return ret;
	}
}
