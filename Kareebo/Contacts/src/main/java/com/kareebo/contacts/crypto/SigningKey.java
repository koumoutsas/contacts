package com.kareebo.contacts.crypto;

import com.kareebo.contacts.thrift.SignatureAlgorithm;

import javax.annotation.Nonnull;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Wrapper around a {@link java.security.PrivateKey} and {@link com.kareebo.contacts.thrift.SignatureAlgorithm}
 */
public class SigningKey
{
	public final PrivateKey key;
	public final SignatureAlgorithm algorithm;

	public SigningKey(final @Nonnull com.kareebo.contacts.thrift.client.SigningKey signingKey) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		algorithm=signingKey.getAlgorithm();
		key=KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(signingKey.getBuffer()));
	}
}
