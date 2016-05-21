package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.crypto.Utils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.security.*;

/**
 * Utility class for generating a key pair
 */
class KeyPair
{
	private final java.security.KeyPair keyPair;

	KeyPair() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException
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

	PublicKey getPublic()
	{
		return keyPair.getPublic();
	}
}
