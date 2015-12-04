package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.EncryptedBufferSignedWithVerificationKey;
import com.kareebo.contacts.thrift.EncryptionKeysWithHashBuffer;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the suggest new contact identity service
 */
class SuggestNewContact extends com.kareebo.contacts.client.jobs.Service
{
	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.SuggestNewContact.method1))
		{
			suggestNewContact1((EncryptionKeysWithHashBuffer)payload,enqueuers);
		}
		else if(method.equals(com.kareebo.contacts.base.service.SuggestNewContact.method3))
		{
			suggestNewContact3((EncryptedBufferSignedWithVerificationKey)payload,enqueuers);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void suggestNewContact1(final EncryptionKeysWithHashBuffer encryptionKeysWithHashBuffer,final Enqueuers enqueuer)
	{
	}

	private void suggestNewContact3(final EncryptedBufferSignedWithVerificationKey encryptedBufferSignedWithVerificationKey,final Enqueuers
		                                                                                                                        enqueuers)
	{
	}
}
