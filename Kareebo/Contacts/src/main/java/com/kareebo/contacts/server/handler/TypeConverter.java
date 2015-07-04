package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.common.UserAgent;
import com.kareebo.contacts.server.gora.Algorithm;
import com.kareebo.contacts.server.gora.CryptoBuffer;
import com.kareebo.contacts.server.gora.PublicKeys;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

/**
 * Utility collection for converting between Gora and Thrift types
 */
class TypeConverter
{
	static PublicKeys convert(final com.kareebo.contacts.common.PublicKeys publicKeys) throws NoSuchAlgorithmException
	{
		final PublicKeys ret=new PublicKeys();
		ret.setEncryption(convert(publicKeys.getEncryption()));
		ret.setVerification(convert(publicKeys.getVerification()));
		return ret;
	}

	static com.kareebo.contacts.common.PublicKeys convert(final PublicKeys publicKeys) throws NoSuchAlgorithmException
	{
		return new com.kareebo.contacts.common.PublicKeys(convert(publicKeys.getEncryption()),convert(publicKeys
			                                                                                              .getVerification
				                                                                                               ()));
	}

	static CryptoBuffer convert(final com.kareebo.contacts.common.CryptoBuffer cryptoBuffer) throws NoSuchAlgorithmException
	{
		final CryptoBuffer ret=new CryptoBuffer();
		ret.setAlgorithm(convert(cryptoBuffer.getAlgorithm()));
		final ByteBuffer buffer=cryptoBuffer.bufferForBuffer();
		buffer.mark();
		ret.setBuffer(buffer);
		return ret;
	}

	static com.kareebo.contacts.common.CryptoBuffer convert(final CryptoBuffer cryptoBuffer) throws NoSuchAlgorithmException
	{
		return new com.kareebo.contacts.common.CryptoBuffer(cryptoBuffer.getBuffer(),convert(cryptoBuffer
			                                                                                     .getAlgorithm()));
	}

	static Algorithm convert(final com.kareebo.contacts.common.Algorithm algorithm) throws NoSuchAlgorithmException
	{
		switch(algorithm)
		{
			case SHA256withECDSAprime239v1:
				return Algorithm.SHA256withECDSAprime239v1;
			case SHA256:
				return Algorithm.SHA256;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
	}

	static com.kareebo.contacts.common.Algorithm convert(final Algorithm algorithm) throws NoSuchAlgorithmException
	{
		switch(algorithm)
		{
			case SHA256withECDSAprime239v1:
				return com.kareebo.contacts.common.Algorithm.SHA256withECDSAprime239v1;
			case SHA256:
				return com.kareebo.contacts.common.Algorithm.SHA256;
			default:
				throw new NoSuchAlgorithmException(algorithm.toString());
		}
	}

	static CharSequence convert(final long id)
	{
		return String.valueOf(id);
	}

	static com.kareebo.contacts.server.gora.UserAgent convert(final UserAgent userAgent)
	{
		final com.kareebo.contacts.server.gora.UserAgent ret=new com.kareebo.contacts.server.gora.UserAgent();
		ret.setPlatform(userAgent.getPlatform());
		ret.setVersion(userAgent.getVersion());
		return ret;
	}

	static UserAgent convert(final com.kareebo.contacts.server.gora.UserAgent userAgent)
	{
		return new UserAgent(userAgent.getPlatform().toString(),userAgent.getVersion().toString());
	}
}
