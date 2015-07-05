package com.kareebo.contacts.base;

import com.google.common.primitives.Longs;
import com.kareebo.contacts.common.HashedContact;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link HashedContact}
 */
public class HashedContactPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=1+CryptoBufferPlaintextSerializer.LENGTH;
	final private HashedContact hashedContact;

	HashedContactPlaintextSerializer(final HashedContact hashedContact)
	{
		this.hashedContact=hashedContact;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		ret.add(Longs.toByteArray(hashedContact.getId()));
		ret.addAll(new CryptoBufferPlaintextSerializer(hashedContact.getHash()).serialize());
		return ret;
	}
}
