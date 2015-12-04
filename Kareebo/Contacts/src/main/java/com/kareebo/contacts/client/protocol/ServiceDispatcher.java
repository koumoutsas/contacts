package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.ClientId;
import org.apache.thrift.async.TAsyncClientManager;

import java.lang.reflect.InvocationTargetException;

/**
 * Service factory for the protocol side
 */
public class ServiceDispatcher extends com.kareebo.contacts.client.jobs.ServiceDispatcher
{
	final private TAsyncClientManager clientManager;
	final private SigningKey signingKey;
	final private ClientId clientId;

	public ServiceDispatcher(final Enqueuers enqueuers,final TAsyncClientManager clientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(enqueuers);
		this.clientManager=clientManager;
		this.signingKey=signingKey;
		this.clientId=clientId;
	}

	@Override
	public Service constructService(final Class<?> theClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		return (Service)theClass.getDeclaredConstructor(TAsyncClientManager.class,SigningKey.class,ClientId.class).newInstance(clientManager,signingKey,
			clientId);
	}
}
