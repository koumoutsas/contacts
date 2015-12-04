package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.EncryptedBufferSignedWithVerificationKey;
import com.kareebo.contacts.thrift.MapClientIdEncryptionKey;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the broadcast new contact identity service
 */
class BroadcastNewContactIdentity extends com.kareebo.contacts.client.jobs.Service
{
	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method1))
		{
			broadcastNewContactIdentity1((MapClientIdEncryptionKey)payload,enqueuers);
		}
		else if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method2))
		{
			broadcastNewContactIdentity2((MapClientIdEncryptionKey)payload,enqueuers);
		}
		else if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method4))
		{
			broadcastNewContactIdentity4((EncryptedBufferSignedWithVerificationKey)payload,enqueuers);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void broadcastNewContactIdentity1(final MapClientIdEncryptionKey clientIdEncryptionKeyMap,final Enqueuers enqueuers)
	{
	}

	private void broadcastNewContactIdentity2(final MapClientIdEncryptionKey clientIdEncryptionKeyMap,final Enqueuers enqueuers)
	{
	}

	private void broadcastNewContactIdentity4(final EncryptedBufferSignedWithVerificationKey encryptedBufferSignedWithVerificationKey,final
	Enqueuers enqueuers)
	{
	}
}
