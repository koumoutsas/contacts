package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.common.CryptoBuffer;
import com.kareebo.contacts.common.PublicKeys;
import com.kareebo.contacts.common.UserAgent;
import com.kareebo.contacts.server.gora.Algorithm;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link TypeConverter} between Gora and Thrift types
 */
public class TypeConverterTest
{
	@Before
	public void setUp() throws Exception
	{
		// Only for getting to 100% coverage
		new TypeConverter();
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
	public void testConvertPublicKeysToThrift() throws Exception
	{
		final byte[] buffer1={'a','b'};
		final com.kareebo.contacts.server.gora.CryptoBuffer encryptionKey=create(Algorithm
			                                                                         .SHA256withECDSAprime239v1,buffer1);
		final byte[] buffer2={'c','d'};
		final com.kareebo.contacts.server.gora.CryptoBuffer verificationKey=create(Algorithm.SHA256,buffer2);
		final com.kareebo.contacts.server.gora.PublicKeys publicKeys=new com.kareebo.contacts.server.gora.PublicKeys();
		publicKeys.setEncryption(encryptionKey);
		publicKeys.setVerification(verificationKey);
		final PublicKeys converted=TypeConverter.convert(publicKeys);
		assertEquals(TypeConverter.convert(encryptionKey),converted.getEncryption());
		assertEquals(TypeConverter.convert(verificationKey),converted.getVerification());
	}

	private CryptoBuffer create(final com.kareebo.contacts.common.Algorithm algorithm,final byte[] buffer)
	{
		final CryptoBuffer cryptoBuffer=new CryptoBuffer();
		cryptoBuffer.setAlgorithm(algorithm);
		cryptoBuffer.setBuffer(buffer);
		return cryptoBuffer;
	}

	private com.kareebo.contacts.server.gora.CryptoBuffer create(final Algorithm algorithm,final byte[] buffer)
	{
		final com.kareebo.contacts.server.gora.CryptoBuffer cryptoBuffer=new com.kareebo.contacts.server.gora.CryptoBuffer();
		cryptoBuffer.setAlgorithm(algorithm);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		cryptoBuffer.setBuffer(byteBuffer);
		return cryptoBuffer;
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
	public void testConvertCryptoBufferToThrift() throws Exception
	{
		final Algorithm algorithm=Algorithm.SHA256withECDSAprime239v1;
		final byte[] buffer={'a','b'};
		final CryptoBuffer converted=TypeConverter.convert(create(algorithm,buffer));
		assertEquals(TypeConverter.convert(algorithm),converted.getAlgorithm());
		assertEquals(ByteBuffer.wrap(buffer),converted.bufferForBuffer());
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testConvertAlgorithm() throws Exception
	{
		assertEquals(Algorithm.SHA256,TypeConverter.convert(com.kareebo.contacts.common.Algorithm.SHA256));
		assertEquals(Algorithm.SHA256withECDSAprime239v1,TypeConverter.convert(com.kareebo.contacts.common.Algorithm.SHA256withECDSAprime239v1));
		TypeConverter.convert(com.kareebo.contacts.common.Algorithm.Fake);
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testConvertAlgorithmToThrift() throws Exception
	{
		assertEquals(com.kareebo.contacts.common.Algorithm.SHA256,TypeConverter.convert(Algorithm.SHA256));
		assertEquals(com.kareebo.contacts.common.Algorithm.SHA256withECDSAprime239v1,TypeConverter.convert(Algorithm.SHA256withECDSAprime239v1));
		TypeConverter.convert(Algorithm.Fake);
	}

	@Test
	public void testConvertLongToCharSequence() throws Exception
	{
		final long expected=12234;
		final long calculated=Long.valueOf((String)(TypeConverter.convert(expected)));
		assertEquals(expected,calculated);
	}

	@Test
	public void testConvertUserAgent() throws Exception
	{
		final UserAgent userAgent=new UserAgent("A","B");
		final com.kareebo.contacts.server.gora.UserAgent converted=TypeConverter.convert(userAgent);
		assertEquals(userAgent.getPlatform(),converted.getPlatform());
		assertEquals(userAgent.getVersion(),converted.getVersion());
	}

	@Test
	public void testConvertUserAgentToThrift() throws Exception
	{
		final com.kareebo.contacts.server.gora.UserAgent userAgent=new com.kareebo.contacts.server.gora
			                                                               .UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		final UserAgent converted=TypeConverter.convert(userAgent);
		assertEquals(userAgent.getPlatform(),converted.getPlatform());
		assertEquals(userAgent.getVersion(),converted.getVersion());
	}
}