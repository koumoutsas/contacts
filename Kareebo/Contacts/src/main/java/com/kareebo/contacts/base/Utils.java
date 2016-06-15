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

	/**
	 * Resolve a class based on a package
	 *
	 * @param className   The class name
	 * @param packageName An optional package name
	 * @return A class whose fully qualified name is package name + class name. If this fails, as a fallback only class name is used to resolve
	 * the class
	 * @throws ClassNotFoundException If no resolution succeeds
	 */
	@Nonnull
	public static Class<?> resolveClass(@Nonnull final String className,final String packageName) throws ClassNotFoundException
	{
		if(packageName!=null)
		{
			try
			{
				return Class.forName(packageName+'.'+className);
			}
			catch(ClassNotFoundException ignored)
			{
			}
		}
		return Class.forName(className);
	}
}