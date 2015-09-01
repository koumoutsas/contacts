package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.EncryptedBufferPair;
import com.kareebo.contacts.thrift.FailedOperation;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link EncryptedBufferPair}
 */
public class EncryptedBufferPairPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=2*EncryptedBufferPlaintextSerializer.LENGTH;
	final private EncryptedBufferPair encryptedBufferPair;

	EncryptedBufferPairPlaintextSerializer(final EncryptedBufferPair encryptedBufferPair)
	{
		this.encryptedBufferPair=encryptedBufferPair;
	}

	@Override
	public Vector<byte[]> serialize() throws FailedOperation
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		ret.addAll(new EncryptedBufferPlaintextSerializer(encryptedBufferPair.getI()).serialize());
		ret.addAll(new EncryptedBufferPlaintextSerializer(encryptedBufferPair.getIR()).serialize());
		return ret;
	}
}
