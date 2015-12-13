package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.EncryptedBufferSignedWithVerificationKey;
import com.kareebo.contacts.thrift.MapClientIdEncryptionKey;
import org.apache.thrift.TBase;

/**
 * Client-processor-side implementation of the broadcast new contact identity service
 */
public class BroadcastNewContactIdentity extends com.kareebo.contacts.client.jobs.Service
{
	private static final String serviceName=BroadcastNewContactIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");
	public final static ServiceMethod method4=new ServiceMethod(serviceName,"4");

	@Override
	protected void runInternal(final com.kareebo.contacts.thrift.client.jobs.ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
	{
		if(method.equals(method1))
		{
			broadcastNewContactIdentity1((MapClientIdEncryptionKey)payload,enqueuers);
		}
		else if(method.equals(method2))
		{
			broadcastNewContactIdentity2((MapClientIdEncryptionKey)payload,enqueuers);
		}
		else if(method.equals(method4))
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
