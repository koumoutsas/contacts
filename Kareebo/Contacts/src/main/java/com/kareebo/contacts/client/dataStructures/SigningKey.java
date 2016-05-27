package com.kareebo.contacts.client.dataStructures;

import com.kareebo.contacts.thrift.SignatureAlgorithm;

import javax.annotation.Nonnull;
import java.security.PrivateKey;

/**
 * Wrapper around a {@link java.security.PrivateKey} and {@link com.kareebo.contacts.thrift.SignatureAlgorithm}
 */
public class SigningKey
{
	public final PrivateKey key;
	public final SignatureAlgorithm algorithm;

	public SigningKey(final @Nonnull PrivateKey key,final @Nonnull SignatureAlgorithm algorithm)
	{
		this.key=key;
		this.algorithm=algorithm;
	}
}
