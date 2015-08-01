package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;

/**
 * {@link PlaintextSerializer} for {@link EncryptionKey}
 */
public class EncryptionKeyPlaintextSerializer extends CryptoPlaintextSerializer<EncryptionAlgorithm>
{
	EncryptionKeyPlaintextSerializer(final EncryptionKey encryptionKey)
	{
		super(encryptionKey.getAlgorithm(),
			     encryptionKey.getBuffer());
	}
}