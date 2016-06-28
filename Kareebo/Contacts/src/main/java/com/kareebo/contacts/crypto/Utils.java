package com.kareebo.contacts.crypto;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.EncryptionAlgorithm;
import com.kareebo.contacts.server.gora.EncryptionKey;
import com.kareebo.contacts.server.gora.SignatureAlgorithm;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.FailedOperation;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
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
		verify.initVerify(KeyFactory.getInstance(characteristics.get(Public)).generatePublic(new X509EncodedKeySpec(com.kareebo.contacts.base.Utils.getBytes(verificationKey.getBuffer()))));
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
			case SHA512withECDSAprime239v1:
				ret.add("ECDSA");
				ret.add("SHA512withECDSA");
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

	/**
	 * {@link Exception} subclass thrown when the algorithm is not supported
	 */
	public static class UnsupportedAlgorithmException extends Exception
	{
	}

	/**
	 * Pair of a {@link Cipher} created with the "RSA/NONE/NoPadding" specification and the {@link RSAPublicKey} used for the cipher
	 */
	public static class RSANoPaddingCipher
	{
		public final Cipher cipher;
		public final RSAPublicKey publicKey;

		/**
		 * Constructor from {@link EncryptionKey}
		 *
		 * @param encryptionKey The encryption key
		 * @throws UnsupportedAlgorithmException When the algorithm of the key is not {@link EncryptionAlgorithm#RSA2048}
		 * @throws NoSuchPaddingException
		 * @throws NoSuchAlgorithmException
		 * @throws NoSuchProviderException
		 * @throws InvalidKeyException
		 * @throws InvalidKeySpecException
		 */
		public RSANoPaddingCipher(final EncryptionKey encryptionKey) throws UnsupportedAlgorithmException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException
		{
			final EncryptionAlgorithm encryptionAlgorithm=encryptionKey.getAlgorithm();
			if(encryptionAlgorithm!=EncryptionAlgorithm.RSA2048)
			{
				throw new UnsupportedAlgorithmException();
			}
			cipher=Cipher.getInstance("RSA/NONE/NoPadding",Utils.getProvider());
			publicKey=(RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(new
				                                                                     X509EncodedKeySpec(com.kareebo.contacts.base.Utils.getBytes
					                                                                                                                        (encryptionKey.getBuffer())));
			cipher.init(Cipher.ENCRYPT_MODE,publicKey);
		}
	}
}
