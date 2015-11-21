package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.HashBufferSet;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit test for {@link RegisterUnconfirmedIdentity}
 */
public class RegisterUnconfirmedIdentityTest extends SimpleTestHarness.SimpleTestBase<HashBufferSet,Void>
{
	public RegisterUnconfirmedIdentityTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
		super("uSet");
	}

	@Override
	protected void perform(final HashBufferSet object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId clientId,final ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		new RegisterUnconfirmedIdentity(clientManager,signingKey,clientId).registerUnconfirmedIdentity1(object,handler);
	}

	@Override
	protected HashBufferSet construct()
	{
		final Set<HashBuffer> hashBuffers=new HashSet<>(2);
		final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
		buffer.mark();
		hashBuffers.add(new HashBuffer(buffer,HashAlgorithm.Fake));
		hashBuffers.add(new HashBuffer(buffer,HashAlgorithm.SHA256));
		return new HashBufferSet(hashBuffers);
	}
}