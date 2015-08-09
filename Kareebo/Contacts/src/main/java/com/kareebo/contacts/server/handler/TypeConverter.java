package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.*;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

/**
 * Utility collection for converting between Gora and Thrift types
 */
class TypeConverter
{
	static PublicKeys convert(final com.kareebo.contacts.thrift.PublicKeys publicKeys) throws
		NoSuchAlgorithmException
	{
		final PublicKeys ret=new PublicKeys();
		ret.setEncryption(convert(publicKeys.getEncryption()));
		ret.setVerification(convert(publicKeys.getVerification()));
		return ret;
	}

	static EncryptionKey convert(final com.kareebo.contacts.thrift.EncryptionKey encryptionKey) throws NoSuchAlgorithmException
	{
		final EncryptionKey ret=new EncryptionKey();
		ret.setAlgorithm(convert(encryptionKey.getAlgorithm()));
		final ByteBuffer buffer=encryptionKey.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	static VerificationKey convert(final com.kareebo.contacts.thrift.VerificationKey verificationKey) throws
		NoSuchAlgorithmException
	{
		final VerificationKey ret=new VerificationKey();
		ret.setAlgorithm(convert(verificationKey.getAlgorithm()));
		final ByteBuffer buffer=verificationKey.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	static EncryptionAlgorithm convert(final com.kareebo.contacts.thrift.EncryptionAlgorithm algorithm) throws
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

	static SignatureAlgorithm convert(final com.kareebo.contacts.thrift.SignatureAlgorithm algorithm) throws
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

	static com.kareebo.contacts.thrift.PublicKeys convert(final PublicKeys publicKeys) throws
		NoSuchAlgorithmException
	{
		return new com.kareebo.contacts.thrift.PublicKeys(convert(publicKeys.getEncryption()),convert(publicKeys
			                                                                                              .getVerification
				                                                                                               ()));
	}

	static com.kareebo.contacts.thrift.EncryptionKey convert(final EncryptionKey encryptionKey) throws NoSuchAlgorithmException
	{
		return new com.kareebo.contacts.thrift.EncryptionKey(encryptionKey.getBuffer(),convert(encryptionKey
			                                                                                       .getAlgorithm()));
	}

	static com.kareebo.contacts.thrift.VerificationKey convert(final VerificationKey verificationKey) throws
		NoSuchAlgorithmException
	{
		return new com.kareebo.contacts.thrift.VerificationKey(verificationKey.getBuffer(),convert(verificationKey
			                                                                                           .getAlgorithm()));
	}

	static com.kareebo.contacts.thrift.EncryptionAlgorithm convert(final EncryptionAlgorithm algorithm) throws
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

	static com.kareebo.contacts.thrift.SignatureAlgorithm convert(final SignatureAlgorithm algorithm) throws
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

	static HashBuffer convert(final com.kareebo.contacts.thrift.HashBuffer hashBuffer) throws
		NoSuchAlgorithmException
	{
		final HashBuffer ret=new HashBuffer();
		ret.setAlgorithm(convert(hashBuffer.getAlgorithm()));
		final ByteBuffer buffer=hashBuffer.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	static HashAlgorithm convert(final com.kareebo.contacts.thrift.HashAlgorithm algorithm) throws
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

	static EncryptedBuffer convert(final com.kareebo.contacts.thrift.EncryptedBuffer encryptedBuffer) throws
		NoSuchAlgorithmException
	{
		final EncryptedBuffer ret=new EncryptedBuffer();
		ret.setAlgorithm(convert(encryptedBuffer.getAlgorithm()));
		final ByteBuffer buffer=encryptedBuffer.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	static com.kareebo.contacts.thrift.EncryptedBuffer convert(final EncryptedBuffer encryptedBuffer) throws
		NoSuchAlgorithmException
	{
		final com.kareebo.contacts.thrift.EncryptedBuffer ret=new com.kareebo.contacts.thrift.EncryptedBuffer();
		ret.setAlgorithm(convert(encryptedBuffer.getAlgorithm()));
		final ByteBuffer buffer=encryptedBuffer.getBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	static com.kareebo.contacts.thrift.HashBuffer convert(final HashBuffer cryptoBuffer) throws
		NoSuchAlgorithmException
	{
		return new com.kareebo.contacts.thrift.HashBuffer(cryptoBuffer.getBuffer(),convert(cryptoBuffer
			                                                                                   .getAlgorithm()));
	}

	static com.kareebo.contacts.thrift.HashAlgorithm convert(final HashAlgorithm algorithm) throws
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

	static CharSequence convert(final long id)
	{
		return String.valueOf(id);
	}

	static UserAgent convert(final com.kareebo.contacts.thrift.UserAgent userAgent)
	{
		final UserAgent ret=new UserAgent();
		ret.setPlatform(userAgent.getPlatform());
		ret.setVersion(userAgent.getVersion());
		return ret;
	}

	static com.kareebo.contacts.thrift.UserAgent convert(final UserAgent userAgent)
	{
		return new com.kareebo.contacts.thrift.UserAgent(userAgent.getPlatform().toString(),userAgent.getVersion().toString());
	}
}
