package com.kareebo.contacts.dataStructures.common;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Create a {@link java.security.PublicKey from a serialized representation}
 */
public class PublicKey
{
	/**
	 * Security provider - Bouncy Castle
	 */
	final static private String provider="BC";
	/**
	 * The key algorithm
	 */
	private final String algorithm;
	/**
	 * The serialized key
	 */
	private final byte[] buffer;
	/**
	 * @return The key
	 */
	private java.security.PublicKey key;

	/**
	 * @param buffer    X509-encoded byte array of the serialized key
	 * @param algorithm The key algorithm
	 */
	public PublicKey(final byte[] buffer,final String algorithm)
	{
		this.algorithm=algorithm;
		this.buffer=buffer;
	}

	/**
	 * Performs lazy initialization of the key
	 *
	 * @return The key
	 * @throws InvalidKeySpecException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 */
	public java.security.PublicKey getKey() throws InvalidKeySpecException, NoSuchProviderException, NoSuchAlgorithmException
	{
		if(key==null)
		{
			final KeyFactory keyFactory=KeyFactory.getInstance(algorithm,provider);
			key=keyFactory.generatePublic(new X509EncodedKeySpec(buffer));
		}
		return key;
	}

	public boolean equals(final Object o)
	{
		if(o==null)
		{
			return false;
		}
		if(this==o)
		{
			return true;
		}
		if(!(o instanceof PublicKey))
		{
			return false;
		}
		final PublicKey p=(PublicKey)o;
		return algorithm==p.algorithm&&key==p.key;
	}
}
