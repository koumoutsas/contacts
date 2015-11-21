package com.kareebo.contacts.client.handler;

import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TException;

import java.security.*;

/**
 * Unit test for {@link ModifyUserAgent}
 */
public class ModifyUserAgentTest extends SimpleTestBase
{
	public ModifyUserAgentTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
	}

	@Override
	protected void run() throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
	{
		new ModifyUserAgent(new com.kareebo.contacts.client.service.ModifyUserAgent(clientManager,signingKey,clientId),successNotifier,
			                   errorNotifier).run(new UserAgent(null,null));
	}

	@Override
	protected String serviceName()
	{
		return ModifyUserAgent.class.getSimpleName();
	}
}