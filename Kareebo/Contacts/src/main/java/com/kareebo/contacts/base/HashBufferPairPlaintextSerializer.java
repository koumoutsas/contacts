package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.HashBufferPair;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link HashBufferPair}
 */
public class HashBufferPairPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=2*HashBufferPlaintextSerializer.LENGTH;
	final private HashBufferPair hashBufferPair;

	public HashBufferPairPlaintextSerializer(final HashBufferPair hashBufferPair)
	{
		this.hashBufferPair=hashBufferPair;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		ret.addAll(new HashBufferPlaintextSerializer(hashBufferPair.getUC()).serialize());
		ret.addAll(new HashBufferPlaintextSerializer(hashBufferPair.getUPrimeC()).serialize());
		return ret;
	}
}
