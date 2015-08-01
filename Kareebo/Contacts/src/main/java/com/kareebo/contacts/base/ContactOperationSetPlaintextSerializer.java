package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.ContactOperation;

import java.util.Set;
import java.util.Vector;

/**
 * {@link PlaintextSerializer} for set of {@link ContactOperation}
 */
public class ContactOperationSetPlaintextSerializer implements PlaintextSerializer
{
	final private Set<ContactOperation> contactOperationSet;

	/**
	 * Constructor
	 *
	 * @param contactOperationSet The contact operation set
	 */
	public ContactOperationSetPlaintextSerializer(final Set<ContactOperation> contactOperationSet)
	{
		this.contactOperationSet=contactOperationSet;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(contactOperationSet.size()*ContactOperationPlaintextSerializer.LENGTH);
		for(ContactOperation contactOperation : contactOperationSet)
		{
			ret.addAll(new ContactOperationPlaintextSerializer(contactOperation).serialize());
		}
		return ret;
	}
}
