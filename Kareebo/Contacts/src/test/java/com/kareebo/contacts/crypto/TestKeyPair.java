package com.kareebo.contacts.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.Provider;
import java.security.Security;

/**
 * Base class for test key pairs
 */
abstract class TestKeyPair
{
	KeyPair keyPair;
	final Provider provider;

	TestKeyPair()
	{
		provider=new BouncyCastleProvider();
		Security.addProvider(provider);
	}
}
