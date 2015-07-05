package com.kareebo.contacts.base;

import com.kareebo.contacts.common.PublicKeys;

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
		final Vector<byte[]> ret=new Vector<>(2*CryptoBufferPlaintextSerializer.LENGTH);
		ret.addAll(new CryptoBufferPlaintextSerializer(publicKeys.getEncryption()).serialize());
		ret.addAll(new CryptoBufferPlaintextSerializer(publicKeys.getVerification()).serialize());
		return ret;
	}
}
