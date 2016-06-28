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

	public RandomHashPad()
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
	ByteBuffer getByteBuffer()
	{
		final ByteBuffer ret=ByteBuffer.wrap(bytes);
		ret.mark();
		return ret;
	}

	/**
	 * Get the byte arrays for the pad
	 * @return {@link #bytes}
	 */
	@Nonnull
	public byte[] getBytes()
	{
		return bytes;
	}
}
