package com.kareebo.contacts.dataStructures.common;

/**
 * Encryption and signature verification key pair
 */
public class PublicKeys
{
	/**
	 * Public key for encryption
	 */
	final private PublicKey encryption;
	/**
	 * Public key for signature verification
	 */
	final private PublicKey verification;

	/**
	 * @param encryptionKey            X509-encoded byte array of the serialized encryption key
	 * @param encryptionKeyAlgorithm   The encryption key algorithm
	 * @param verificationKey          X509-encoded byte array of the serialized signature verification key
	 * @param verificationKeyAlgorithm The signature verification key algorithm
	 */
	public PublicKeys(final byte[] encryptionKey,final String encryptionKeyAlgorithm,final byte[]
		                                                                                 verificationKey,
	                  final String verificationKeyAlgorithm)
	{
		encryption=new PublicKey(encryptionKey,encryptionKeyAlgorithm);
		verification=new PublicKey(verificationKey,verificationKeyAlgorithm);
	}

	/**
	 * @return The verification key
	 */
	public PublicKey getVerification()
	{
		return verification;
	}

	/**
	 * @return The encryption key
	 */
	public PublicKey getEncryption()
	{
		return encryption;
	}
}
