package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.PublicKeys;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link PublicKeys}
 */
public class PublicKeysPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=EncryptionKeyPlaintextSerializer.LENGTH+VerificationKeyPlaintextSerializer.LENGTH;
	final private PublicKeys publicKeys;

	public PublicKeysPlaintextSerializer(final PublicKeys publicKeys)
	{
		this.publicKeys=publicKeys;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		ret.addAll(new EncryptionKeyPlaintextSerializer(publicKeys.getEncryption()).serialize());
		ret.addAll(new VerificationKeyPlaintextSerializer(publicKeys.getVerification()).serialize());
		return ret;
	}
}
