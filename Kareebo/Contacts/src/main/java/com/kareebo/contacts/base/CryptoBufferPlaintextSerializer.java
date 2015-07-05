package com.kareebo.contacts.base;

import com.kareebo.contacts.common.CryptoBuffer;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link com.kareebo.contacts.common.CryptoBuffer}
 */
public class CryptoBufferPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=AlgorithmPlaintextSerializer.LENGTH+1;
	final private CryptoBuffer cryptoBuffer;

	CryptoBufferPlaintextSerializer(final CryptoBuffer cryptoBuffer)
	{
		this.cryptoBuffer=cryptoBuffer;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		ret.addAll(new AlgorithmPlaintextSerializer(cryptoBuffer.getAlgorithm()).serialize());
		ret.add(cryptoBuffer.getBuffer());
		return ret;
	}
}
