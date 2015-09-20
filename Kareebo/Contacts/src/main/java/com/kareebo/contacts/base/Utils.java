package com.kareebo.contacts.base;

import com.kareebo.contacts.server.gora.HashBuffer;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Various utilities
 */
public class Utils
{
	public static Set<HashBuffer> convertToSet(final List<HashBuffer> list)
	{
		final Set<HashBuffer> ret=new HashSet<>(list.size());
		for(final HashBuffer h : list)
		{
			final HashBuffer newH=new HashBuffer();
			newH.setAlgorithm(h.getAlgorithm());
			final ByteBuffer b=h.getBuffer();
			b.rewind();
			b.mark();
			newH.setBuffer(b);
			ret.add(newH);
		}
		return ret;
	}
}