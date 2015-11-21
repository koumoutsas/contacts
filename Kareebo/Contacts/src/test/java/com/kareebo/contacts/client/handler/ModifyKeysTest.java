package com.kareebo.contacts.client.handler;

import com.kareebo.contacts.thrift.PublicKeys;
import org.apache.thrift.TException;

import java.security.*;

/**
 * Unit test for {@link ModifyKeys}
 */
public class ModifyKeysTest extends SimpleTestBase
{
	public ModifyKeysTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
	}

	@Override
	protected void run() throws NoSuchAlgorithmException, TException, NoSuchProviderException, InvalidKeyException, SignatureException
	{
		new ModifyKeys(new com.kareebo.contacts.client.service.ModifyKeys(clientManager,signingKey,clientId),successNotifier,
			              errorNotifier).run(new PublicKeys(null,null));
	}

	@Override
	protected String serviceName()
	{
		return ModifyKeys.class.getSimpleName();
	}
}