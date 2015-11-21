package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.ResultHandler;
import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TException;

import java.security.*;

/**
 * Unit test for {@link ModifyUserAgent}
 */
public class ModifyUserAgentTest extends SimpleTestHarness.SimpleTestBase<UserAgent,Void>
{
	public ModifyUserAgentTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
		super("userAgent");
	}

	@Override
	protected void perform(final UserAgent object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId
		                                                                                                                      clientId,final
	                       ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException,
		                                                           SignatureException
	{
		new ModifyUserAgent(clientManager,signingKey,clientId).modifyUserAgent1(object,handler);
	}

	@Override
	protected UserAgent construct()
	{
		return new UserAgent("a","b");
	}
}