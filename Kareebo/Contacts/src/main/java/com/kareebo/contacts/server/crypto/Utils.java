package com.kareebo.contacts.server.crypto;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.FailedOperation;

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
	 * @param verificationKey     The verification key
	 * @param signature           The signature
	 * @param plaintextSerializer The plaintext serializer
	 * @return Whether the signature is correct
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	public static boolean verifySignature(final VerificationKey verificationKey,final ByteBuffer signature,final
	PlaintextSerializer plaintextSerializer)
		throws
		NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException, FailedOperation
	{
		final Vector<String> characteristics=decompose(verificationKey.getAlgorithm());
		final Signature verify=Signature.getInstance(characteristics.get(1),provider);
		final ByteBuffer verificationByteBuffer=verificationKey.getBuffer();
		verificationByteBuffer.rewind();
		final byte[] verificationBytes=new byte[verificationByteBuffer.remaining()];
		verificationByteBuffer.get(verificationBytes);
		verify.initVerify(KeyFactory.getInstance(characteristics.get(0)).generatePublic(new X509EncodedKeySpec(
			                                                                                                      verificationBytes)));
		for(final Object aPlaintext : plaintextSerializer.serialize())
		{
			verify.update((byte[])aPlaintext);
		}
		final byte[] signatureBytes=new byte[signature.remaining()];
		signature.get(signatureBytes);
		return verify.verify(signatureBytes);
	}

	/**
	 * Break an {@link SignatureAlgorithm} to strings that can be passed to the Java crypto API
	 *
	 * @param algorithm The {@link SignatureAlgorithm}
	 * @return A list of strings that can be passed to the Java API. Their placement in the list is case-dependent
	 */
	private static Vector<String> decompose(final SignatureAlgorithm algorithm) throws NoSuchAlgorithmException
	{
		final Vector<String> ret=new Vector<>(2);
		switch(algorithm)
		{
			case SHA256withECDSAprime239v1:
				ret.add("ECDSA");
				ret.add("SHA256withECDSA");
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

	/**
	 * XOR two byte arrays
	 *
	 * @param a The first array
	 * @param b The second array
	 * @return The XOR of the two inputs. If one is shorter, it's padded with 0s
	 */
	public static byte[] xor(final byte[] a,final byte[] b)
	{
		byte[] longer, shorter;
		if(a.length>b.length)
		{
			longer=a;
			shorter=b;
		}
		else
		{
			longer=b;
			shorter=a;
		}
		final byte[] ret=new byte[longer.length];
		int i;
		for(i=0;i<shorter.length;++i)
		{
			ret[i]=(byte)(longer[i]^shorter[i]);
		}
		for(;i<longer.length;++i)
		{
			ret[i]=longer[i];
		}
		return ret;
	}
}
