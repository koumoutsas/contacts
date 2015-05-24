package com.kareebo.contacts.dataStructures.common;

/**
 * Encapsulates all the information needed to create a {@link com.kareebo.contacts.dataStructures.common.Handle}. Use this class to serialize/deserialize a
 * Handle
 */
public class HandleConstructor
{
	/**
	 * The serialized object
	 */
	private final String buffer;
	/**
	 * The type of handle
	 */
	private final HandleType type;

	/**
	 * @param buffer The serialized object
	 * @param type   The type of handle
	 */
	public HandleConstructor(final String buffer,final HandleType type)
	{
		this.buffer=buffer;
		this.type=type;
	}

	/**
	 * @return The serialized object
	 */
	public String getBuffer()
	{
		return buffer;
	}

	/**
	 * @return The type of handle
	 */
	public HandleType getType()
	{
		return type;
	}
}
