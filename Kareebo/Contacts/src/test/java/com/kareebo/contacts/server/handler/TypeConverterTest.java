package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.common.CryptoBuffer;
import com.kareebo.contacts.common.PublicKeys;
import com.kareebo.contacts.server.gora.Algorithm;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link TypeConverter} between Gora and Thrift types
 */
public class TypeConverterTest
{
	private CryptoBuffer create(final com.kareebo.contacts.common.Algorithm algorithm,final byte[] buffer)
	{
		final CryptoBuffer cryptoBuffer=new CryptoBuffer();
		cryptoBuffer.setAlgorithm(algorithm);
		cryptoBuffer.setBuffer(buffer);
		return cryptoBuffer;
	}

	@Test
	public void testConvertPublicKeys() throws Exception
	{
		final byte[] buffer1={'a','b'};
		final CryptoBuffer encryptionKey=create(com.kareebo.contacts.common.Algorithm
			                                        .SHA256withECDSAprime239v1,buffer1);
		final byte[] buffer2={'c','d'};
		final CryptoBuffer verificationKey=create(com.kareebo.contacts.common.Algorithm.SHA256,buffer2);
		final PublicKeys publicKeys=new PublicKeys();
		publicKeys.setEncryption(encryptionKey);
		publicKeys.setVerification(verificationKey);
		final com.kareebo.contacts.server.gora.PublicKeys converted=TypeConverter.convert(publicKeys);
		assertEquals(TypeConverter.convert(encryptionKey),converted.getEncryption());
		assertEquals(TypeConverter.convert(verificationKey),converted.getVerification());
	}

	@Test
	public void testConvertCryptoBuffer() throws Exception
	{
		final com.kareebo.contacts.common.Algorithm algorithm=com.kareebo.contacts.common.Algorithm
			                                                      .SHA256withECDSAprime239v1;
		final byte[] buffer={'a','b'};
		final com.kareebo.contacts.server.gora.CryptoBuffer converted=TypeConverter.convert(create(algorithm,buffer));
		assertEquals(TypeConverter.convert(algorithm),converted.getAlgorithm());
		assertEquals(ByteBuffer.wrap(buffer),converted.getBuffer());
	}

	@Test
	public void testConvertAlgorithm() throws Exception
	{
		assertEquals(Algorithm.SHA256,TypeConverter.convert(com.kareebo.contacts.common.Algorithm.SHA256));
		assertEquals(Algorithm.SHA256withECDSAprime239v1,TypeConverter.convert(com.kareebo.contacts.common.Algorithm.SHA256withECDSAprime239v1));
	}

	@Test
	public void testConvertLongToCharSequence() throws Exception
	{
		final long expected=12234;
		final long calculated=Long.valueOf((String)(TypeConverter.convert(expected)));
		assertEquals(expected,calculated);
	}
}