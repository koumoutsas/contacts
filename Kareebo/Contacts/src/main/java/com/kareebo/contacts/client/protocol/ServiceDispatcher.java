package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.client.jobs.Context;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service factory for the protocol side
 */
public class ServiceDispatcher extends com.kareebo.contacts.client.jobs.ServiceDispatcher
{
	final private static String packageName=ServiceDispatcher.class.getPackage().getName()+".";
	final private Map<Class<?>,TAsyncClientManager> clientManagers=new HashMap<>();
	final private SigningKey signingKey;
	final private ClientId clientId;

	/**
	 * Create a service dispatcher
	 *
	 * @param enqueuers  The enqueuers for the services
	 * @param signingKey The signing key for payloads
	 * @param clientId   The client id of the endpoint
	 */
	public ServiceDispatcher(final @Nonnull Enqueuers enqueuers,final @Nonnull SigningKey signingKey,final @Nonnull ClientId clientId)
	{
		super(enqueuers);
		this.signingKey=signingKey;
		this.clientId=clientId;
	}

	/**
	 * Add a {@link TAsyncClientManager} for a service
	 *
	 * @param service            The service name
	 * @param asyncClientManager The {@link TAsyncClientManager}
	 * @throws ClassNotFoundException When a class with the service name cannot be found
	 * @throws DuplicateService       When a service is added twice
	 */
	public void add(@Nonnull final String service,@Nonnull TAsyncClientManager asyncClientManager) throws ClassNotFoundException, DuplicateService
	{
		final Class<?> serviceClass=resolve(service);
		if(clientManagers.containsKey(serviceClass))
		{
			throw new DuplicateService(service);
		}
		clientManagers.put(serviceClass,asyncClientManager);
	}

	/**
	 * Resolve the service class by name. First it looks for it in the package and if not found it interprets the service name as an absolute
	 * class name
	 *
	 * @param service The service name
	 * @return A {@link Class}
	 * @throws ClassNotFoundException If the class is not in the package or it doesn't point to a reachable absolute class path
	 */
	private static Class<?> resolve(@Nonnull final String service) throws ClassNotFoundException
	{
		try
		{
			return Class.forName(packageName+service);
		}
		catch(ClassNotFoundException ignored)
		{
			return Class.forName(service);
		}
	}

	/**
	 * Remove all services from the handled set
	 */
	public void clear()
	{
		clientManagers.clear();
	}

	@Nonnull
	@Override
	protected Service constructService(@Nonnull final Class<?> service,final Context context) throws NoSuchMethodException,
		                                                                                                 IllegalAccessException,
		                                                                                                 InvocationTargetException, InstantiationException, NoSuchService
	{
		final TAsyncClientManager clientManager=clientManagers.get(service);
		if(clientManager==null)
		{
			throw new NoSuchService();
		}
		return (Service)service.getDeclaredConstructor(Context.class,TAsyncClientManager.class,SigningKey.class,ClientId.class).newInstance
			                                                                                                                        (context,clientManager,signingKey,
				                                                                                                                        clientId);
	}

	/**
	 * Thrown when a service is registered twice with {@link #add(String,TAsyncClientManager)}
	 */
	public static class DuplicateService extends Exception
	{
		DuplicateService(final String service)
		{
			super(service+" already configured");
		}
	}
}
