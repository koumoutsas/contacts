package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Service factory for the protocol side
 */
public class ServiceDispatcher extends com.kareebo.contacts.client.jobs.ServiceDispatcher
{
	final private TAsyncClientManager clientManager;
	final private SigningKey signingKey;
	final private ClientId clientId;

	ServiceDispatcher(final Enqueuers enqueuers,final TAsyncClientManager clientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(enqueuers);
		this.clientManager=clientManager;
		this.signingKey=signingKey;
		this.clientId=clientId;
	}

	@Override
	public Service constructService(@Nonnull final Class<?> theClass,final Context context) throws NoSuchMethodException, IllegalAccessException,
		                                                                                      InvocationTargetException, InstantiationException
	{
		return (Service)theClass.getDeclaredConstructor(Context.class,TAsyncClientManager.class,SigningKey.class,ClientId.class).newInstance
			                                                                                                                         (context,clientManager,signingKey,
				                                                                                                                         clientId);
	}
}
