package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.EncryptedBufferSignedWithVerificationKey;
import com.kareebo.contacts.thrift.EncryptionKeys;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the send contact card service
 */
class SendContactCard extends com.kareebo.contacts.client.jobs.Service
{
	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.SendContactCard.method2))
		{
			sendContactCard2((EncryptionKeys)payload,enqueuers);
		}
		else if(method.equals(com.kareebo.contacts.base.service.SendContactCard.method4))
		{
			sendContactCard4((EncryptedBufferSignedWithVerificationKey)payload,enqueuers);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void sendContactCard2(final EncryptionKeys encryptionKeys,final Enqueuers enqueuers)
	{
	}

	private void sendContactCard4(final EncryptedBufferSignedWithVerificationKey encryptedBufferSignedWithVerificationKey,final Enqueuers
		                                                                                                                      enqueuers)
	{
	}
}
