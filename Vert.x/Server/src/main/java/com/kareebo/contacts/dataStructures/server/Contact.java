package com.kareebo.contacts.dataStructures.server;

import com.kareebo.contacts.dataStructures.common.Id;

/**
 * Server-side representation of a contact
 */
public class Contact
{
	/**
	 * Contact internal id
	 */
	final Id id;
	/**
	 * The contact hash
	 */
	final byte[] hash;
	/**
	 * The hash algorithm
	 */
	final String algorithm;

	/**
	 * @param id        Contact internal id
	 * @param hash      The contact hash
	 * @param algorithm The hash algorithm
	 */
	public Contact(final Id id,final byte[] hash,final String algorithm)
	{
		this.id=id;
		this.hash=hash;
		this.algorithm=algorithm;
	}

	/**
	 * @return Contact internal id
	 */
	public Id getId()
	{
		return id;
	}

	/**
	 * @return The contact hash
	 */
	public byte[] getHash()
	{
		return hash;
	}

	/**
	 * @return The hash algorithm
	 */
	public String getAlgorithm()
	{
		return algorithm;
	}
}
