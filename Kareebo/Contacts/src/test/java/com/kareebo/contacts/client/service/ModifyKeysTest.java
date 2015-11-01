package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.security.*;

/**
 * Unit test for {@link ModifyKeys}
 */
public class ModifyKeysTest extends SimpleTestBase<PublicKeys>
{
	public ModifyKeysTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
	}

	@Override
	void perform(final PublicKeys object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<Void> asyncResultHandler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		new ModifyKeys(clientManager,signingKey,clientId).modifyKeys1(object,asyncResultHandler);
	}

	@Override
	protected PublicKeys construct()
	{
		final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
		buffer.mark();
		return new PublicKeys(new EncryptionKey(buffer,EncryptionAlgorithm.RSA2048),new VerificationKey(buffer,SignatureAlgorithm.Fake));
	}

	@Override
	protected String getFieldName()
	{
		return "newPublicKeys";
	}
}