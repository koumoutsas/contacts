package com.kareebo.contacts.base;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * Random 128-bit byte buffer
 */
public class RandomHashPad
{
	private final byte[] bytes=new byte[16];

	/**
	 * Get the random bytes
	 */
	public
	@Nonnull
	RandomHashPad()
	{
		new SecureRandom().nextBytes(bytes);
	}

	/**
	 * Get a {@link ByteBuffer} wrapping the random bytes
	 *
	 * @return A {@link ByteBuffer} wrapping the random bytes
	 */
	public
	@Nonnull
	ByteBuffer getBytes()
	{
		final ByteBuffer ret=ByteBuffer.wrap(bytes);
		ret.mark();
		return ret;
	}
}
