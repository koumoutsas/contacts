package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.crypto.Utils;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.security.*;

/**
 * Base class for signing Thrift objects
 */
class Signer
{
	private final SigningKey signingKey;
	private final ClientId clientId;

	Signer(final SigningKey signingKey,final ClientId clientId)
	{
		this.signingKey=signingKey;
		this.clientId=clientId;
	}

	SignatureBuffer sign(final TBase object) throws TException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
	{
		final Signature ecdsaSign=Signature.getInstance(Utils.getSignatureAlgorithm(signingKey.algorithm),Utils.getProvider());
		ecdsaSign.initSign(signingKey.key);
		ecdsaSign.update(new TSerializer().serialize(object));
		final SignatureBuffer signatureBuffer=new SignatureBuffer();
		signatureBuffer.setBuffer(ecdsaSign.sign());
		signatureBuffer.setAlgorithm(signingKey.algorithm);
		signatureBuffer.setClient(clientId);
		return signatureBuffer;
	}
}
