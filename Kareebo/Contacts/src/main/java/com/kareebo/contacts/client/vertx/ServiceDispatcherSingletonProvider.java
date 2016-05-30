package com.kareebo.contacts.client.vertx;

import com.kareebo.contacts.client.protocol.Enqueuers;
import com.kareebo.contacts.client.protocol.ServiceDispatcher;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.ClientId;

import javax.annotation.Nonnull;

/**
 * Provides a singleton of {@link ServiceDispatcher}
 */
class ServiceDispatcherSingletonProvider
{
	private static ServiceDispatcher serviceDispatcher;

	/**
	 * Construct the singleton if one is not constructed already. It is not thread-safe, thread safety is the responsibility of the caller. It
	 * should be used from the {@link Verticle} to initialize the runtime
	 *
	 * @param enqueuers  The enqueuers for the services
	 * @param signingKey The signing key for payloads
	 * @param clientId   The client id of the endpoint
	 * @return The singleton
	 */
	@Nonnull
	static ServiceDispatcher get(final @Nonnull Enqueuers enqueuers,final @Nonnull SigningKey signingKey,final @Nonnull ClientId clientId) throws DuplicateInitialization
	{
		if(serviceDispatcher!=null)
		{
			throw new DuplicateInitialization();
		}
		serviceDispatcher=new ServiceDispatcher(enqueuers,signingKey,clientId);
		return serviceDispatcher;
	}

	/**
	 * Get the singleton only if it has been initialized already. It should be used by the job dispatcher framework
	 *
	 * @return The singleton
	 * @throws Uninitialized If {@link #get(Enqueuers,SigningKey,ClientId)} has not been called before
	 */
	@Nonnull
	public static ServiceDispatcher get() throws Uninitialized
	{
		if(serviceDispatcher==null)
		{
			throw new Uninitialized();
		}
		return serviceDispatcher;
	}

	/**
	 * Clears the singleton, so that {@link #get(Enqueuers,SigningKey,ClientId)} can be called again
	 */
	public static void reset()
	{
		serviceDispatcher=null;
	}

	/**
	 * Exception thrown when the singleton is requested with {@link #get()} before it has been initialized with {@link #get(Enqueuers,SigningKey,ClientId)}
	 */
	static class Uninitialized extends Exception
	{
	}

	/**
	 * Exception thrown when {@link #get(Enqueuers,SigningKey,ClientId)} is called twice
	 */
	static class DuplicateInitialization extends Exception
	{
	}
}
