package com.kareebo.contacts.base;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * Collection of utility methods
 */
public class Utils
{
	/**
	 * Get the bytes from a ByteBuffer
	 *
	 * @param byteBuffer The ByteBuffer object. It gets rewound before being copied
	 * @return A byte array
	 */
	public static
	@Nonnull
	byte[] getBytes(final @Nonnull ByteBuffer byteBuffer)
	{
		byteBuffer.rewind();
		final byte[] ret=new byte[byteBuffer.remaining()];
		byteBuffer.get(ret);
		return ret;
	}
}