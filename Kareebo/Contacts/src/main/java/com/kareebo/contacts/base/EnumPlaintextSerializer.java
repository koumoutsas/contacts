package com.kareebo.contacts.base;

import org.apache.thrift.TEnum;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for algorithm enums
 */
public class EnumPlaintextSerializer<T extends TEnum> implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public final static int LENGTH=1;
	final private T algorithm;

	EnumPlaintextSerializer(final T algorithm)
	{
		this.algorithm=algorithm;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		final byte[] bytes=new byte[1];
		bytes[0]=(byte)algorithm.getValue();
		ret.add(bytes);
		return ret;
	}
}