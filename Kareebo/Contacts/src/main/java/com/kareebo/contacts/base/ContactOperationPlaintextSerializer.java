package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.ContactOperation;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link ContactOperation}
 */
public class ContactOperationPlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=HashBufferPlaintextSerializer.LENGTH+EnumPlaintextSerializer
		                                                                    .LENGTH;
	final private ContactOperation contactOperation;

	ContactOperationPlaintextSerializer(final ContactOperation contactOperation)
	{
		this.contactOperation=contactOperation;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		ret.addAll(new HashBufferPlaintextSerializer(contactOperation.getContact()).serialize());
		ret.addAll(new EnumPlaintextSerializer<>(contactOperation.getType()).serialize());
		return ret;
	}
}
