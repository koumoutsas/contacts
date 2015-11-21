package com.kareebo.contacts.client.handler;

import com.kareebo.contacts.thrift.HashBufferSet;
import org.apache.thrift.TException;

import java.security.*;

/**
 * Unit test for {@link RegisterUnconfirmedIdentity}
 */
public class RegisterUnconfirmedIdentityTest extends SimpleTestBase
{
	public RegisterUnconfirmedIdentityTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
	}

	@Override
	protected void run() throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
	{
		new RegisterUnconfirmedIdentity(new com.kareebo.contacts.client.service.RegisterUnconfirmedIdentity(clientManager,signingKey,
			                                                                                                   clientId),
			                               successNotifier,errorNotifier).run(new HashBufferSet());
	}

	@Override
	protected String serviceName()
	{
		return RegisterUnconfirmedIdentity.class.getSimpleName();
	}
}