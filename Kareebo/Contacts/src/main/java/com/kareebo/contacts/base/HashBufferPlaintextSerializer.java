package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;

/**
 * {@link PlaintextSerializer} for {@link HashBuffer}
 */
public class HashBufferPlaintextSerializer extends CryptoPlaintextSerializer<HashAlgorithm>
{
	public HashBufferPlaintextSerializer(final HashBuffer hashBuffer)
	{
		super(hashBuffer.getAlgorithm(),hashBuffer.getBuffer());
	}
}