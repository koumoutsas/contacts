package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashBuffer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Collection of utility methods
 */
class Utils
{
	/**
	 * Utility method for converting a list of {@link HashBuffer}s to a set
	 *
	 * @param list The list of {@link HashBuffer}s
	 * @return A set with shallow copies of the {@link HashBuffer}s that can be used on their own
	 */
	static Set<HashBuffer> convertToSet(final List<HashBuffer> list)
	{
		return list.stream().map(h->{
				final HashBuffer newH=new HashBuffer();
				newH.setAlgorithm(h.getAlgorithm());
				final ByteBuffer b=h.getBuffer();
				b.rewind();
				b.mark();
				newH.setBuffer(b);
				return newH;
			}
		).collect(Collectors.toSet());
	}
}
