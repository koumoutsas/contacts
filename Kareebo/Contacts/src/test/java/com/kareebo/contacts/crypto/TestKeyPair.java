package com.kareebo.contacts.crypto;

import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Utility class for generating a random key pair
 */
public class TestKeyPair
{
	private final java.security.KeyPair keyPair;

	public TestKeyPair() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g=KeyPairGenerator.getInstance("ECDSA",Utils.getProvider());
		g.initialize(ecSpec,new SecureRandom());
		keyPair=g.generateKeyPair();
	}

	public PrivateKey getPrivate()
	{
		return keyPair.getPrivate();
	}

	/**
	 * Generate a {@link SigningKey} from the {@link PrivateKey}
	 *
	 * @return A {@link SigningKey}
	 */
	public SigningKey signingKey() throws InvalidKeySpecException, NoSuchAlgorithmException
	{
		final com.kareebo.contacts.thrift.client.SigningKey signingKey=new com.kareebo.contacts.thrift.client.SigningKey();
		signingKey.setAlgorithm(SignatureAlgorithm.SHA512withECDSAprime239v1);
		final ByteBuffer buffer=ByteBuffer.wrap(new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()).getEncoded());
		buffer.mark();
		signingKey.setBuffer(buffer);
		return new SigningKey(signingKey);
	}

	/**
	 * Generate a datastore {@link VerificationKey}
	 *
	 * @return A valid {@link VerificationKey}
	 */
	public VerificationKey verificationKey()
	{
		final VerificationKey verificationKey=new VerificationKey();
		verificationKey.setAlgorithm(com.kareebo.contacts.server.gora.SignatureAlgorithm.SHA512withECDSAprime239v1);
		final ByteBuffer buffer=ByteBuffer.wrap(new X509EncodedKeySpec(getPublic().getEncoded()).getEncoded());
		buffer.mark();
		verificationKey.setBuffer(buffer);
		return verificationKey;
	}

	public PublicKey getPublic()
	{
		return keyPair.getPublic();
	}
}
