package com.kareebo.contacts.base;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link Long}
 */
public class LongPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=StringPlaintextSerializer.LENGTH;
	final private Long i;

	public LongPlaintextSerializer(final Long i)
	{
		this.i=i;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		return new StringPlaintextSerializer(String.valueOf(i)).serialize();
	}
}
