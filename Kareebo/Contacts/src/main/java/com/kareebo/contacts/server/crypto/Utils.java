package com.kareebo.contacts.server.crypto;

import com.kareebo.contacts.server.gora.Algorithm;
import com.kareebo.contacts.server.gora.CryptoBuffer;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Vector;

/**
 * Cryptographic utility methods
 */
public class Utils
{
	/**
	 * Crypto provider Bouncy Castle
	 */
	private static final String provider="BC";

	/**
	 * Verify a signature
	 *
	 * @param verificationKey The verification key
	 * @param signature       The signature
	 * @param plaintext       The plaintext as a list of byte arrays
	 * @return Whether the signature is correct
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	public static boolean verifySignature(final CryptoBuffer verificationKey,final byte[] signature,final
	Vector<byte[]> plaintext)
		throws
		NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException
	{
		final Vector<String> characteristics=decompose(verificationKey.getAlgorithm());
		if(characteristics.size()!=2)
		{
			throw new NoSuchAlgorithmException();
		}
		final Signature verify=Signature.getInstance(characteristics.get(1),provider);
		final ByteBuffer byteBuffer=verificationKey.getBuffer();
		final byte[] bb=new byte[byteBuffer.remaining()];
		byteBuffer.get(bb);
		verify.initVerify(KeyFactory.getInstance(characteristics.get(0)).generatePublic(new X509EncodedKeySpec(
			                                                                                                      bb)));
		for(final Object aPlaintext : plaintext)
		{
			verify.update((byte[])aPlaintext);
		}
		return verify.verify(signature);
	}

	/**
	 * Break an {@link Algorithm} to strings that can be passed to the Java crypto API
	 *
	 * @param algorithm The {@link Algorithm}
	 * @return A list of strings that can be passed to the Java API. Their placement in the list is case-dependent
	 */
	private static Vector<String> decompose(final Algorithm algorithm) throws NoSuchAlgorithmException
	{
		final Vector<String> ret=new Vector<>(2);
		switch(algorithm)
		{
			case SHA256withECDSAprime239v1:
				ret.add("ECDSA");
				ret.add("SHA256withECDSA");
				break;
			case SHA256:
				ret.add("SHA256");
				break;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
		return ret;
	}

	/**
	 * Get the security provider
	 *
	 * @return The security provider's name
	 */
	public static String getProvider()
	{
		return provider;
	}
}
