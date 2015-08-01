package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.*;
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
		final com.kareebo.contacts.thrift.EncryptionKey encryptionKey=create(
			                                                                    com.kareebo.contacts.thrift.EncryptionAlgorithm.RSA2048,
		buffer1);
		final byte[] buffer2={'c','d'};
		final com.kareebo.contacts.thrift.VerificationKey verificationKey=create(com.kareebo.contacts.thrift.SignatureAlgorithm
			                                                           .SHA256withECDSAprime239v1,buffer2);
		final com.kareebo.contacts.thrift.PublicKeys publicKeys=new com.kareebo.contacts.thrift.PublicKeys(encryptionKey,
			                                                                                                  verificationKey);
		final PublicKeys converted=TypeConverter.convert(publicKeys);
		assertEquals(TypeConverter.convert(encryptionKey),converted.getEncryption());
		assertEquals(TypeConverter.convert(verificationKey),converted.getVerification());
	}

	@Test
	public void testConvertPublicKeysToThrift() throws Exception
	{
		final byte[] buffer1={'a','b'};
		final EncryptionKey encryptionKey=create(EncryptionAlgorithm.RSA2048,buffer1);
		final byte[] buffer2={'c','d'};
		final VerificationKey verificationKey=create(SignatureAlgorithm.SHA256withECDSAprime239v1,buffer2);
		final PublicKeys publicKeys=new PublicKeys();
		publicKeys.setEncryption(encryptionKey);
		publicKeys.setVerification(verificationKey);
		final com.kareebo.contacts.thrift.PublicKeys converted=TypeConverter.convert(publicKeys);
		assertEquals(TypeConverter.convert(encryptionKey),converted.getEncryption());
		assertEquals(TypeConverter.convert(verificationKey),converted.getVerification());
	}

	private com.kareebo.contacts.thrift.EncryptionKey create(final com.kareebo.contacts.thrift.EncryptionAlgorithm
		                                                         algorithm,final byte[] buffer)
	{
		final com.kareebo.contacts.thrift.EncryptionKey encryptionKey=new com.kareebo.contacts.thrift
			                                                                  .EncryptionKey();
		encryptionKey.setAlgorithm(algorithm);
		encryptionKey.setBuffer(buffer);
		return encryptionKey;
	}

	private com.kareebo.contacts.thrift.VerificationKey create(final com.kareebo.contacts.thrift
		                                                                 .SignatureAlgorithm
		                                                         algorithm,final byte[] buffer)
	{
		final com.kareebo.contacts.thrift.VerificationKey verificationKey=new com.kareebo.contacts.thrift
			                                                                  .VerificationKey();
		verificationKey.setAlgorithm(algorithm);
		verificationKey.setBuffer(buffer);
		return verificationKey;
	}

	private com.kareebo.contacts.thrift.HashBuffer create(final com.kareebo.contacts.thrift.HashAlgorithm
		                                                         algorithm,final byte[] buffer)
	{
		final com.kareebo.contacts.thrift.HashBuffer hashBuffer=new com.kareebo.contacts.thrift
			                                                                  .HashBuffer();
		hashBuffer.setAlgorithm(algorithm);
		hashBuffer.setBuffer(buffer);
		return hashBuffer;
	}

	private EncryptionKey create(final EncryptionAlgorithm algorithm,final byte[] buffer)
	{
		final EncryptionKey encryptionKey=new EncryptionKey();
		encryptionKey.setAlgorithm(algorithm);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		encryptionKey.setBuffer(byteBuffer);
		return encryptionKey;
	}

	private VerificationKey create(final SignatureAlgorithm algorithm,final byte[] buffer)
	{
		final VerificationKey verificationKey=new VerificationKey();
		verificationKey.setAlgorithm(algorithm);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		verificationKey.setBuffer(byteBuffer);
		return verificationKey;
	}

	private HashBuffer create(final HashAlgorithm algorithm,final byte[] buffer)
	{
		final HashBuffer hashBuffer=new HashBuffer();
		hashBuffer.setAlgorithm(algorithm);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		hashBuffer.setBuffer(byteBuffer);
		return hashBuffer;
	}

	@Test
	public void testConvertEncryptionKey() throws Exception
	{
		final com.kareebo.contacts.thrift.EncryptionAlgorithm algorithm=com.kareebo.contacts.thrift
			                                                                .EncryptionAlgorithm.RSA2048;
		final byte[] buffer={'a','b'};
		final EncryptionKey converted=TypeConverter.convert(create(algorithm,buffer));
		assertEquals(TypeConverter.convert(algorithm),converted.getAlgorithm());
		assertEquals(ByteBuffer.wrap(buffer),converted.getBuffer());
	}

	@Test
	public void testConvertVerificationKey() throws Exception
	{
		final com.kareebo.contacts.thrift.SignatureAlgorithm algorithm=com.kareebo.contacts.thrift
			                                                               .SignatureAlgorithm.SHA256withECDSAprime239v1;
		final byte[] buffer={'a','b'};
		final VerificationKey converted=TypeConverter.convert(create(algorithm,buffer));
		assertEquals(TypeConverter.convert(algorithm),converted.getAlgorithm());
		assertEquals(ByteBuffer.wrap(buffer),converted.getBuffer());
	}

	@Test
	public void testConvertHashBuffer() throws Exception
	{
		final com.kareebo.contacts.thrift.HashAlgorithm algorithm=com.kareebo.contacts.thrift.HashAlgorithm.SHA256;
		final byte[] buffer={'a','b'};
		final HashBuffer converted=TypeConverter.convert(create(algorithm,buffer));
		assertEquals(TypeConverter.convert(algorithm),converted.getAlgorithm());
		assertEquals(ByteBuffer.wrap(buffer),converted.getBuffer());
	}

	@Test
	public void testConvertEncryptionKeyToThrift() throws Exception
	{
		final EncryptionAlgorithm algorithm=EncryptionAlgorithm.RSA2048;
		final byte[] buffer={'a','b'};
		final com.kareebo.contacts.thrift.EncryptionKey converted=TypeConverter.convert(create(algorithm,buffer));
		assertEquals(TypeConverter.convert(algorithm),converted.getAlgorithm());
		assertEquals(ByteBuffer.wrap(buffer),converted.bufferForBuffer());
	}

	@Test
	public void testConvertVerificationKeyToThrift() throws Exception
	{
		final SignatureAlgorithm algorithm=SignatureAlgorithm.SHA256withECDSAprime239v1;
		final byte[] buffer={'a','b'};
		final com.kareebo.contacts.thrift.VerificationKey converted=TypeConverter.convert(create(algorithm,buffer));
		assertEquals(TypeConverter.convert(algorithm),converted.getAlgorithm());
		assertEquals(ByteBuffer.wrap(buffer),converted.bufferForBuffer());
	}

	@Test
	public void testConvertHashBufferToThrift() throws Exception
	{
		final HashAlgorithm algorithm=HashAlgorithm.SHA256;
		final byte[] buffer={'a','b'};
		final com.kareebo.contacts.thrift.HashBuffer converted=TypeConverter.convert(create(algorithm,buffer));
		assertEquals(TypeConverter.convert(algorithm),converted.getAlgorithm());
		assertEquals(ByteBuffer.wrap(buffer),converted.bufferForBuffer());
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testConvertEncryptionAlgorithm() throws Exception
	{
		assertEquals(EncryptionAlgorithm.RSA2048,TypeConverter.convert(com.kareebo.contacts.thrift
			                                                               .EncryptionAlgorithm
			                                                          .RSA2048));
		TypeConverter.convert(com.kareebo.contacts.thrift.EncryptionAlgorithm.Fake);
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testConvertSignatureAlgorithm() throws Exception
	{
		assertEquals(SignatureAlgorithm.SHA256withECDSAprime239v1,TypeConverter.convert(com.kareebo.contacts.thrift
			                                                             .SignatureAlgorithm.SHA256withECDSAprime239v1));
		TypeConverter.convert(com.kareebo.contacts.thrift.SignatureAlgorithm.Fake);
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testConvertHashAlgorithm() throws Exception
	{
		assertEquals(HashAlgorithm.SHA256,TypeConverter.convert(com.kareebo.contacts.thrift.HashAlgorithm.SHA256));
		TypeConverter.convert(com.kareebo.contacts.thrift.HashAlgorithm.Fake);
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testConvertEncryptionAlgorithmToThrift() throws Exception
	{
		assertEquals(com.kareebo.contacts.thrift.EncryptionAlgorithm.RSA2048,TypeConverter.convert
			                                                                                   (EncryptionAlgorithm
			                                                                                      .RSA2048));
		TypeConverter.convert(EncryptionAlgorithm.Fake);
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testConvertSignatureAlgorithmToThrift() throws Exception
	{
		assertEquals(com.kareebo.contacts.thrift.SignatureAlgorithm.SHA256withECDSAprime239v1,TypeConverter.convert
			                                                                                   (SignatureAlgorithm.SHA256withECDSAprime239v1));
		TypeConverter.convert(SignatureAlgorithm.Fake);
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testConvertHashAlgorithmToThrift() throws Exception
	{
		assertEquals(com.kareebo.contacts.thrift.HashAlgorithm.SHA256,TypeConverter.convert(HashAlgorithm.SHA256));
		TypeConverter.convert(HashAlgorithm.Fake);
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
		final com.kareebo.contacts.thrift.UserAgent userAgent=new com.kareebo.contacts.thrift.UserAgent("A","B");
		final UserAgent converted=TypeConverter.convert(userAgent);
		assertEquals(userAgent.getPlatform(),converted.getPlatform());
		assertEquals(userAgent.getVersion(),converted.getVersion());
	}

	@Test
	public void testConvertUserAgentToThrift() throws Exception
	{
		final UserAgent userAgent=new UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		final com.kareebo.contacts.thrift.UserAgent converted=TypeConverter.convert(userAgent);
		assertEquals(userAgent.getPlatform(),converted.getPlatform());
		assertEquals(userAgent.getVersion(),converted.getVersion());
	}
}