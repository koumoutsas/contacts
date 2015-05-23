package com.kareebo.contacts.dataStructures.common;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Encryption and signature verification elliptic curve keys. It uses Bouncy Castle as the provider
 */
public class PublicKeys
{
	/**
	 * Key algorithm - Elliptic curve
	 */
	final static private String algorithm="ECDSA";
	/**
	 * Security provider - Bouncy Castle
	 */
	final static private String provider="BC";
	/**
	 * Public key for encryption
	 */
	final private PublicKey encryption;
	/**
	 * Public key for signature verification
	 */
	final private PublicKey verification;

	/**
	 * @param encryptionKey   X509-encoded byte array to initialize the encryption key
	 * @param verificationKey X509-encoded byte array to initialize the signature verification key
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchProviderException
	 */
	public PublicKeys(final byte[] encryptionKey,final byte[] verificationKey) throws NoSuchAlgorithmException,
		                                                                                  InvalidKeySpecException, NoSuchProviderException
	{
		final KeyFactory keyFactory=KeyFactory.getInstance(algorithm,provider);
		encryption=keyFactory.generatePublic(new X509EncodedKeySpec(encryptionKey));
		verification=keyFactory.generatePublic(new X509EncodedKeySpec(verificationKey));
	}

	/**
	 * @return The security provider
	 */
	public static String getProvider()
	{
		return provider;
	}

	/**
	 * @return The key algorithm
	 */
	public static String getAlgorithm()
	{
		return algorithm;
	}

	/**
	 * @return The encryption key
	 */
	public PublicKey getEncryption()
	{
		return encryption;
	}

	/**
	 * @return The signature verification key
	 */
	public PublicKey getVerification()
	{
		return verification;
	}
}
