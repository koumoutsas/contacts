package com.kareebo.contacts.crypto;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.FailedOperation;

import javax.annotation.Nonnull;
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
	/// The index of the public string in the result of decompose
	private static final int Public=0;
	/// The index of the private string in the result of decompose
	private static final int Private=1;

	/**
	 * Verify a signature
	 *
	 * @param verificationKey The verification key
	 * @param signature       The signature
	 * @param plaintext       The plaintext
	 * @return Whether the signature is correct
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	public static boolean verifySignature(final @Nonnull VerificationKey verificationKey,final @Nonnull ByteBuffer signature,final @Nonnull byte[] plaintext)
		throws
		NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException, FailedOperation
	{
		final Vector<String> characteristics=decompose(verificationKey.getAlgorithm());
		final Signature verify=Signature.getInstance(characteristics.get(Private),provider);
		final byte[] verificationBytes=com.kareebo.contacts.base.Utils.getBytes(verificationKey.getBuffer());
		verify.initVerify(KeyFactory.getInstance(characteristics.get(Public)).generatePublic(new X509EncodedKeySpec(verificationBytes)));
		verify.update(plaintext);
		final byte[] signatureBytes=new byte[signature.remaining()];
		signature.get(signatureBytes);
		return verify.verify(signatureBytes);
	}

	/**
	 * Break an {@link SignatureAlgorithm} to strings that can be passed to the Java crypto API
	 *
	 * @param algorithm The {@link SignatureAlgorithm}
	 * @return A list of strings that can be passed to the Java API. First comes the public part, then the private
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
	public static
	@Nonnull
	String getProvider()
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
	public static
	@Nonnull
	byte[] xor(final @Nonnull byte[] a,final @Nonnull byte[] b)
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

	/**
	 * Get a string that can be passed to the Java crypto API from {@link com.kareebo.contacts.thrift.SignatureAlgorithm} to
	 *
	 * @param algorithm The {@link com.kareebo.contacts.thrift.SignatureAlgorithm}
	 * @return A string that can be passed to the Java API
	 * @throws NoSuchAlgorithmException
	 */
	public static
	@Nonnull
	String getSignatureAlgorithm(final @Nonnull com.kareebo.contacts.thrift.SignatureAlgorithm algorithm) throws NoSuchAlgorithmException
	{
		return decompose(TypeConverter.convert(algorithm)).elementAt(Private);
	}
}
