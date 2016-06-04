package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.crypto.Utils;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.Assert.fail;

/**
 * Utility for creating signing keys and signing payloads
 */
class Signer
{
	final protected VerificationKey verificationKey=new VerificationKey();
	private KeyPair keyPair;

	Signer()
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g;
		try
		{
			g=KeyPairGenerator.getInstance("ECDSA",Utils.getProvider());
			g.initialize(ecSpec,new SecureRandom());
			keyPair=g.generateKeyPair();
			setUpVerificationKey(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()).getEncoded());
		}
		catch(NoSuchAlgorithmException|NoSuchProviderException|InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	private void setUpVerificationKey(final byte[] buffer)
	{
		verificationKey.setAlgorithm(com.kareebo.contacts.server.gora.SignatureAlgorithm.SHA512withECDSAprime239v1);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		verificationKey.setBuffer(byteBuffer);
	}

	SignatureBuffer sign(final TBase object,final ClientId clientId) throws NoSuchProviderException,
		                                                                        NoSuchAlgorithmException, InvalidKeyException, SignatureException, TException
	{
		return sign(new TSerializer().serialize(object),clientId);
	}

	SignatureBuffer sign(final byte[] buffer,final ClientId clientId) throws NoSuchProviderException,
		                                                                         NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		final Signature ecdsaSign=Signature.getInstance("SHA512withECDSA",Utils.getProvider());
		ecdsaSign.initSign(keyPair.getPrivate());
		ecdsaSign.update(buffer);
		final SignatureBuffer signatureBuffer=new SignatureBuffer();
		signatureBuffer.setBuffer(ecdsaSign.sign());
		signatureBuffer.setAlgorithm(SignatureAlgorithm.SHA512withECDSAprime239v1);
		signatureBuffer.setClient(clientId);
		return signatureBuffer;
	}
}
