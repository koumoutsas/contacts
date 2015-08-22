package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.HashBuffer;

import java.util.Set;
import java.util.Vector;

/**
 * {@link PlaintextSerializer} for a {@link Set} of {@link HashBuffer}
 */
public class SetHashBufferPlaintextSerializer implements PlaintextSerializer
{
	private Set<HashBuffer> hashBufferSet;

	public SetHashBufferPlaintextSerializer(final Set<HashBuffer> hashBufferSet)
	{
		this.hashBufferSet=hashBufferSet;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(HashBufferPlaintextSerializer.LENGTH);
		for(HashBuffer h : hashBufferSet)
		{
			ret.addAll(new HashBufferPlaintextSerializer(h).serialize());
		}
		return ret;
	}
}
