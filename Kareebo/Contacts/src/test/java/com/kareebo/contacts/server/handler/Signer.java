package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.crypto.TestSignatureKeyPair;
import com.kareebo.contacts.crypto.Utils;
import com.kareebo.contacts.server.gora.VerificationKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.security.*;

import static org.junit.Assert.fail;

/**
 * Utility for creating signing keys and signing payloads
 */
class Signer
{
	protected VerificationKey verificationKey;
	private TestSignatureKeyPair keyPair;

	Signer()
	{
		try
		{
			keyPair=new TestSignatureKeyPair();
			verificationKey=keyPair.verificationKey();
		}
		catch(InvalidAlgorithmParameterException|NoSuchProviderException|NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			fail();
		}
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
