package com.kareebo.contacts.client;

import com.kareebo.contacts.thrift.SignatureAlgorithm;

import java.security.PrivateKey;

/**
 * Wrapper around a {@link java.security.PrivateKey} and {@link com.kareebo.contacts.thrift.SignatureAlgorithm}
 */
public class SigningKey
{
	public final PrivateKey key;
	public final SignatureAlgorithm algorithm;

	public SigningKey(final PrivateKey key,final SignatureAlgorithm algorithm)
	{
		this.key=key;
		this.algorithm=algorithm;
	}
}
