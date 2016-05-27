package com.kareebo.contacts.base;

import com.kareebo.contacts.server.gora.*;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

/**
 * Utility collection for converting between Gora and Thrift types
 */
public class TypeConverter
{
	public static
	@Nonnull
	PublicKeys convert(final @Nonnull com.kareebo.contacts.thrift.PublicKeys publicKeys) throws
		NoSuchAlgorithmException
	{
		final PublicKeys ret=new PublicKeys();
		ret.setEncryption(convert(publicKeys.getEncryption()));
		ret.setVerification(convert(publicKeys.getVerification()));
		return ret;
	}

	public static
	@Nonnull
	EncryptionKey convert(final @Nonnull com.kareebo.contacts.thrift.EncryptionKey encryptionKey) throws NoSuchAlgorithmException
	{
		final EncryptionKey ret=new EncryptionKey();
		ret.setAlgorithm(convert(encryptionKey.getAlgorithm()));
		final ByteBuffer buffer=encryptionKey.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	public static
	@Nonnull
	VerificationKey convert(final @Nonnull com.kareebo.contacts.thrift.VerificationKey verificationKey) throws
		NoSuchAlgorithmException
	{
		final VerificationKey ret=new VerificationKey();
		ret.setAlgorithm(convert(verificationKey.getAlgorithm()));
		final ByteBuffer buffer=verificationKey.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	public static
	@Nonnull
	EncryptionAlgorithm convert(final @Nonnull com.kareebo.contacts.thrift.EncryptionAlgorithm algorithm) throws
		NoSuchAlgorithmException
	{
		switch(algorithm)
		{
			case RSA2048:
				return EncryptionAlgorithm.RSA2048;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
	}

	public static
	@Nonnull
	SignatureAlgorithm convert(final @Nonnull com.kareebo.contacts.thrift.SignatureAlgorithm algorithm) throws
		NoSuchAlgorithmException
	{
		switch(algorithm)
		{
			case SHA256withECDSAprime239v1:
				return SignatureAlgorithm.SHA256withECDSAprime239v1;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.PublicKeys convert(final @Nonnull PublicKeys publicKeys) throws
		NoSuchAlgorithmException
	{
		return new com.kareebo.contacts.thrift.PublicKeys(convert(publicKeys.getEncryption()),convert(publicKeys
			                                                                                              .getVerification
				                                                                                               ()));
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.EncryptionKey convert(final @Nonnull EncryptionKey encryptionKey) throws NoSuchAlgorithmException
	{
		final ByteBuffer copy=ByteBuffer.wrap(Utils.getBytes(encryptionKey.getBuffer()));
		copy.mark();
		return new com.kareebo.contacts.thrift.EncryptionKey(copy,convert(encryptionKey
			                                                                  .getAlgorithm()));
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.VerificationKey convert(final @Nonnull VerificationKey verificationKey) throws
		NoSuchAlgorithmException
	{
		final ByteBuffer copy=ByteBuffer.wrap(Utils.getBytes(verificationKey.getBuffer()));
		copy.mark();
		return new com.kareebo.contacts.thrift.VerificationKey(copy,convert(verificationKey.getAlgorithm()));
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.EncryptionAlgorithm convert(final @Nonnull EncryptionAlgorithm algorithm) throws
		NoSuchAlgorithmException
	{
		switch(algorithm)
		{
			case RSA2048:
				return com.kareebo.contacts.thrift.EncryptionAlgorithm.RSA2048;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.SignatureAlgorithm convert(final @Nonnull SignatureAlgorithm algorithm) throws
		NoSuchAlgorithmException
	{
		switch(algorithm)
		{
			case SHA256withECDSAprime239v1:
				return com.kareebo.contacts.thrift.SignatureAlgorithm.SHA256withECDSAprime239v1;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
	}

	public static
	@Nonnull
	HashBuffer convert(final @Nonnull com.kareebo.contacts.thrift.HashBuffer hashBuffer) throws
		NoSuchAlgorithmException
	{
		final HashBuffer ret=new HashBuffer();
		ret.setAlgorithm(convert(hashBuffer.getAlgorithm()));
		final ByteBuffer buffer=hashBuffer.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	public static
	@Nonnull
	HashAlgorithm convert(final @Nonnull com.kareebo.contacts.thrift.HashAlgorithm algorithm) throws
		NoSuchAlgorithmException
	{
		switch(algorithm)
		{
			case SHA256:
				return HashAlgorithm.SHA256;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
	}

	public static
	@Nonnull
	EncryptedBuffer convert(final @Nonnull com.kareebo.contacts.thrift.EncryptedBuffer encryptedBuffer) throws
		NoSuchAlgorithmException
	{
		final EncryptedBuffer ret=new EncryptedBuffer();
		ret.setAlgorithm(convert(encryptedBuffer.getAlgorithm()));
		final ByteBuffer buffer=encryptedBuffer.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.EncryptedBuffer convert(final @Nonnull EncryptedBuffer encryptedBuffer) throws
		NoSuchAlgorithmException
	{
		final com.kareebo.contacts.thrift.EncryptedBuffer ret=new com.kareebo.contacts.thrift.EncryptedBuffer();
		ret.setAlgorithm(convert(encryptedBuffer.getAlgorithm()));
		final ByteBuffer buffer=encryptedBuffer.getBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.HashBuffer convert(final @Nonnull HashBuffer cryptoBuffer) throws
		NoSuchAlgorithmException
	{
		final ByteBuffer copy=ByteBuffer.wrap(Utils.getBytes(cryptoBuffer.getBuffer()));
		copy.mark();
		return new com.kareebo.contacts.thrift.HashBuffer(copy,convert(cryptoBuffer.getAlgorithm()));
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.HashAlgorithm convert(final @Nonnull HashAlgorithm algorithm) throws
		NoSuchAlgorithmException
	{
		switch(algorithm)
		{
			case SHA256:
				return com.kareebo.contacts.thrift.HashAlgorithm.SHA256;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
	}

	public static
	@Nonnull
	CharSequence convert(final long id)
	{
		return String.valueOf(id);
	}

	public static
	@Nonnull
	Long convert(final @Nonnull CharSequence id)
	{
		return Long.parseLong(id.toString());
	}

	public static
	@Nonnull
	UserAgent convert(final @Nonnull com.kareebo.contacts.thrift.UserAgent userAgent)
	{
		final UserAgent ret=new UserAgent();
		ret.setPlatform(userAgent.getPlatform());
		ret.setVersion(userAgent.getVersion());
		return ret;
	}

	public static
	@Nonnull
	com.kareebo.contacts.thrift.UserAgent convert(final @Nonnull UserAgent userAgent)
	{
		return new com.kareebo.contacts.thrift.UserAgent(userAgent.getPlatform().toString(),userAgent.getVersion().toString());
	}
}
