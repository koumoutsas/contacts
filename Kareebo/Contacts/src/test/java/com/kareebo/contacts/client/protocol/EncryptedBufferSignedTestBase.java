package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.thrift.EncryptedBuffer;
import com.kareebo.contacts.thrift.EncryptedBufferSigned;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.SetEncryptedBuffer;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for factoring out duplicate test code
 */
class EncryptedBufferSignedTestBase extends SimpleTestHarness.CollectionSimpleTestBase<EncryptedBufferSigned,Void>
{
	final private ServiceMethod serviceMethod;

	EncryptedBufferSignedTestBase(final ServiceMethod serviceMethod) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
		                                                                        NoSuchProviderException
	{
		super("encryptedBuffers","encryptedBuffer");
		this.serviceMethod=serviceMethod;
	}

	@Override
	protected ServiceMethod getServiceMethod()
	{
		return serviceMethod;
	}

	@Override
	SetEncryptedBuffer constructPayload()
	{
		final Set<EncryptedBuffer> set=new HashSet<>(2);
		final ByteBuffer b1=ByteBuffer.wrap("a".getBytes());
		b1.mark();
		set.add(new EncryptedBuffer(b1,EncryptionAlgorithm.Fake,clientId));
		final ByteBuffer b2=ByteBuffer.wrap("b".getBytes());
		b2.mark();
		set.add(new EncryptedBuffer(b2,EncryptionAlgorithm.Fake,clientId));
		return new SetEncryptedBuffer(set);
	}

	@Override
	protected ServiceMethod nextServiceMethod()
	{
		return null;
	}
}
