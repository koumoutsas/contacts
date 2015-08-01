package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptedBuffer;

/**
 * {@link PlaintextSerializer} for {@link EncryptedBuffer}
 */
public class EncryptedBufferPlaintextSerializer extends CryptoPlaintextSerializer<EncryptionAlgorithm>
{
	EncryptedBufferPlaintextSerializer(final EncryptedBuffer encryptedBuffer)
	{
		super(encryptedBuffer.getAlgorithm(),
			     encryptedBuffer.getBuffer());
	}
}