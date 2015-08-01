package com.kareebo.contacts.base;

import org.apache.thrift.TEnum;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} base class for crypto structures
 */
public abstract class CryptoPlaintextSerializer<T extends TEnum> implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=EnumPlaintextSerializer.LENGTH+1;
	final private byte[] buffer;
	final private T algorithm;

	CryptoPlaintextSerializer(final T algorithm,final byte[] buffer)
	{
		this.algorithm=algorithm;
		this.buffer=buffer;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		ret.addAll(new EnumPlaintextSerializer<>(algorithm).serialize());
		ret.add(buffer);
		return ret;
	}
}
