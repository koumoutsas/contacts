package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit test for {@link UpdateServerContactBook}
 */
public class UpdateServerContactBookTest extends SimpleTestHarness.SimpleTestBase<ContactOperationSet,Void>
{
	public UpdateServerContactBookTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
		super("contactOperationSet");
	}

	@Override
	protected void perform(final ContactOperationSet object,final MockClientManager<Void> clientManager,final SigningKey signingKey,final ClientId clientId,final AsyncResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		new UpdateServerContactBook(clientManager,signingKey,clientId).updateServerContactBook1(object,handler);
	}

	@Override
	protected ContactOperationSet construct()
	{
		final ClientId clientId=new ClientId(9,9);
		final ByteBuffer buffer=ByteBuffer.wrap("a".getBytes());
		buffer.mark();
		final Set<ContactOperation> contactOperations=new HashSet<>(2);
		contactOperations.add(new ContactOperation(new HashBuffer(buffer,HashAlgorithm.SHA256),ContactOperationType.Add,new EncryptedBuffer
			                                                                                                                (buffer,
				                                                                                                                EncryptionAlgorithm.RSA2048,
				                                                                                                                clientId)));
		contactOperations.add(new ContactOperation(new HashBuffer(buffer,HashAlgorithm.SHA256),ContactOperationType.Delete,new
			                                                                                                                   EncryptedBuffer(buffer,EncryptionAlgorithm.RSA2048,
				                                                                                                                                  clientId)));
		return new ContactOperationSet(contactOperations);
	}
}