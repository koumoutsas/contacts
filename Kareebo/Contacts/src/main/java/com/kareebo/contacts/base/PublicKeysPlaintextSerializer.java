package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.PublicKeys;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link PublicKeys}
 */
public class PublicKeysPlaintextSerializer implements PlaintextSerializer
{
	final private PublicKeys publicKeys;

	public PublicKeysPlaintextSerializer(final PublicKeys publicKeys)
	{
		this.publicKeys=publicKeys;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(2*EncryptionKeyPlaintextSerializer.LENGTH);
		ret.addAll(new EncryptionKeyPlaintextSerializer(publicKeys.getEncryption()).serialize());
		ret.addAll(new VerificationKeyPlaintextSerializer(publicKeys.getVerification()).serialize());
		return ret;
	}
}
