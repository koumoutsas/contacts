package com.kareebo.contacts.base;

import com.kareebo.contacts.common.PublicKeys;

import java.util.Vector;

/**
 * Plaintext serializer for user agent
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
		final Vector<byte[]> ret=new Vector<>(2);
		ret.add(publicKeys.getEncryption().getBuffer());
		ret.add(publicKeys.getVerification().getBuffer());
		return ret;
	}
}
