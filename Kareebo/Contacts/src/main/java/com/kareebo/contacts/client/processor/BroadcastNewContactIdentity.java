package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.EncryptedBufferSignedWithVerificationKey;
import com.kareebo.contacts.thrift.MapClientIdEncryptionKey;
import com.kareebo.contacts.thrift.ServiceMethod;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the broadcast new contact identity service
 */
class BroadcastNewContactIdentity extends com.kareebo.contacts.client.jobs.Service
{
	@Override
	protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
	{
		if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method1))
		{
			broadcastNewContactIdentity1((MapClientIdEncryptionKey)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method2))
		{
			broadcastNewContactIdentity2((MapClientIdEncryptionKey)payload,enqueuer);
		}
		else if(method.equals(com.kareebo.contacts.base.service.BroadcastNewContactIdentity.method4))
		{
			broadcastNewContactIdentity4((EncryptedBufferSignedWithVerificationKey)payload,enqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}
	
	private void broadcastNewContactIdentity1(final MapClientIdEncryptionKey clientIdEncryptionKeyMap,final Enqueuer enqueuer)
	{
	}

	private void broadcastNewContactIdentity2(final MapClientIdEncryptionKey clientIdEncryptionKeyMap,final Enqueuer enqueuer)
	{
	}

	private void broadcastNewContactIdentity4(final EncryptedBufferSignedWithVerificationKey encryptedBufferSignedWithVerificationKey,final Enqueuer enqueuer)
	{
	}
}
