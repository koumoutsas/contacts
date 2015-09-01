package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.ContactOperation;
import com.kareebo.contacts.thrift.EncryptedBufferPair;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.HashBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link java.util.Collection}
 */
public class CollectionPlaintextSerializer<E> implements PlaintextSerializer
{
	private static final Logger logger=LoggerFactory.getLogger(CollectionPlaintextSerializer.class.getName());
	private final Collection<E> collection;

	public CollectionPlaintextSerializer(final Collection<E> collection)
	{
		this.collection=collection;
	}

	@Override
	public Vector<byte[]> serialize() throws FailedOperation
	{
		final Vector<byte[]> ret=new Vector<>();
		for(final E e : collection)
		{
			PlaintextSerializer p;
			if(e instanceof HashBuffer)
			{
				p=new HashBufferPlaintextSerializer((HashBuffer)e);
			}
			else if(e instanceof ContactOperation)
			{
				p=new ContactOperationPlaintextSerializer((ContactOperation)e);
			}
			else if(e instanceof EncryptedBufferPair)
			{
				p=new EncryptedBufferPairPlaintextSerializer((EncryptedBufferPair)e);
			}
			else
			{
				logger.error("Unknown class "+e.getClass().getCanonicalName());
				throw new FailedOperation();
			}
			ret.addAll(p.serialize());
		}
		return ret;
	}
}
