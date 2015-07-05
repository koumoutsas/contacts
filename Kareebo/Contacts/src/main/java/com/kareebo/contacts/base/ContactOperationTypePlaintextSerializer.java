package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.ContactOperationType;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link ContactOperationType}
 */
public class ContactOperationTypePlaintextSerializer implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public static final int LENGTH=1;
	final private ContactOperationType contactOperationType;

	ContactOperationTypePlaintextSerializer(final ContactOperationType contactOperationType)
	{
		this.contactOperationType=contactOperationType;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		final byte[] bytes=new byte[1];
		switch(contactOperationType)
		{
			case Add:
				bytes[0]=0;
				break;
			case Delete:
				bytes[0]=1;
				break;
		}
		ret.addElement(bytes);
		return ret;
	}
}
