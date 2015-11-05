package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TException;

import java.security.*;

/**
 * Unit test for {@link ModifyUserAgent}
 */
public class ModifyUserAgentTest extends SimpleTestBase<UserAgent>
{
	public ModifyUserAgentTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
	}

	@Override
	void perform(final UserAgent object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId clientId,final
	AsyncResultHandler<Void> asyncResultHandler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException,
		                                                    SignatureException
	{
		new ModifyUserAgent(clientManager,signingKey,clientId).modifyUserAgent1(object,asyncResultHandler);
	}

	@Override
	protected UserAgent construct()
	{
		return new UserAgent("a","b");
	}

	@Override
	protected String getFieldName()
	{
		return "userAgent";
	}
}