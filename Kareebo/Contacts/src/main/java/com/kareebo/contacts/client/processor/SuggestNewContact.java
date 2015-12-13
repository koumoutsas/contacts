package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.EncryptedBufferSignedWithVerificationKey;
import com.kareebo.contacts.thrift.EncryptionKeysWithHashBuffer;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the suggest new contact identity service
 */
public class SuggestNewContact extends com.kareebo.contacts.client.jobs.Service
{
	public static final String serviceName=SuggestNewContact.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");

	@Override
	protected void runInternal(final com.kareebo.contacts.thrift.client.jobs.ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
	{
		if(method.equals(method1))
		{
			suggestNewContact1((EncryptionKeysWithHashBuffer)payload,enqueuers);
		}
		else if(method.equals(method3))
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
