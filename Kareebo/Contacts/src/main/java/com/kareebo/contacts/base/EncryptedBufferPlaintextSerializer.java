package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.EncryptedBuffer;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;

/**
 * {@link PlaintextSerializer} for {@link EncryptedBuffer}
 */
public class EncryptedBufferPlaintextSerializer extends CryptoPlaintextSerializer<EncryptionAlgorithm>
{
	public EncryptedBufferPlaintextSerializer(final EncryptedBuffer encryptedBuffer)
	{
		super(encryptedBuffer.getAlgorithm(),
			     encryptedBuffer.getBuffer());
	}
}