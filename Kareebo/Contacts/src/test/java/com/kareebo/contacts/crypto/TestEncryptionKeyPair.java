package com.kareebo.contacts.crypto;

import com.kareebo.contacts.server.gora.EncryptionAlgorithm;
import com.kareebo.contacts.server.gora.EncryptionKey;

import java.nio.ByteBuffer;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Utility class for generating a random encryption key pair
 */
public class TestEncryptionKeyPair extends TestKeyPair
{
	public TestEncryptionKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException
	{
		keyPair=KeyPairGenerator.getInstance("RSA",provider.getName()).generateKeyPair();
	}

	public EncryptionKey getEncryptionKey()
	{
		final EncryptionKey encryptionKey=new EncryptionKey();
		encryptionKey.setAlgorithm(EncryptionAlgorithm.RSA2048);
		final ByteBuffer b=ByteBuffer.wrap(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()).getEncoded());
		b.mark();
		encryptionKey.setBuffer(b);
		return encryptionKey;
	}
}
