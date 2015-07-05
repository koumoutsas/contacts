package com.kareebo.contacts.base;

import com.kareebo.contacts.common.Algorithm;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link Algorithm}
 */
public class AlgorithmPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public final static int LENGTH=1;
	final private Algorithm algorithm;

	AlgorithmPlaintextSerializer(final Algorithm algorithm)
	{
		this.algorithm=algorithm;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		final byte[] bytes=new byte[1];
		switch(algorithm)
		{
			case SHA256:
				bytes[0]=0;
				break;
			case SHA256withECDSAprime239v1:
				bytes[0]=1;
				break;
			default:
				bytes[0]=2;
		}
		ret.add(bytes);
		return ret;
	}
}
