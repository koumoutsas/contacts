package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.VerificationKey;

/**
 * {@link PlaintextSerializer} for {@link VerificationKey}
 */
public class VerificationKeyPlaintextSerializer extends CryptoPlaintextSerializer<SignatureAlgorithm>
{
	VerificationKeyPlaintextSerializer(final VerificationKey verificationKey)
	{
		super(verificationKey
			      .getAlgorithm(),verificationKey.getBuffer());
	}
}
