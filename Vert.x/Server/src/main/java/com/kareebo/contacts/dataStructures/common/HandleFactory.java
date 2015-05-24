package com.kareebo.contacts.dataStructures.common;

/**
 * Factory for creating objects implementing the {@link com.kareebo.contacts.dataStructures.common.Handle} interface
 */
public class HandleFactory
{
	/**
	 * @param handleConstructor The constructor information for the {@link com.kareebo.contacts.dataStructures.common.Handle}
	 * @return A {@link com.kareebo.contacts.dataStructures.common.Handle}
	 * @throws IllegalArgumentException if the type is unknown or the argument is null
	 */
	public static Handle create(final HandleConstructor handleConstructor)
	{
		if(handleConstructor==null)
		{
			throw new IllegalArgumentException("Null argument");
		}
		switch(handleConstructor.getType())
		{
			case EmailAddress:
				return new EmailAddress(handleConstructor.getBuffer());
			default:
				throw new IllegalArgumentException("Unknown type");
		}
	}
}
