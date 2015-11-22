package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.crypto.Utils;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.security.*;

/**
 * Utility class for verifying signed payloads
 */
class Verifier
{
	/**
	 * Crypto provider Bouncy Castle
	 */
	private static final String provider="BC";
	private final PublicKey key;
	private final String algorithm;

	Verifier(final PublicKey key,final SignatureAlgorithm algorithm) throws NoSuchAlgorithmException
	{
		this.key=key;
		this.algorithm=Utils.getSignatureAlgorithm(algorithm);
	}

	boolean verify(final TBase object,final byte[] signature) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, TException
	{
		final Signature verify=Signature.getInstance(algorithm,provider);
		verify.initVerify(key);
		verify.update(new TSerializer().serialize(object));
		return verify.verify(signature);
	}
}
