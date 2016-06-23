package com.kareebo.contacts.server.vertx;

import com.google.inject.*;
import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.base.vertx.ServiceStarter;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.PendingNotification;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.server.handler.ClientNotifier;
import com.kareebo.contacts.server.handler.ClientNotifierBackend;
import com.kareebo.contacts.server.handler.GraphAccessor;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.TEventBusServer;
import org.apache.thrift.server.THttpServer;
import org.apache.thrift.server.TServer;
import org.vertx.java.core.logging.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Verticle extension that launches a specific service
 */
abstract public class Verticle extends com.kareebo.contacts.base.vertx.Verticle
{
	private final List<TServer> servers=new ArrayList<>();
	private final Configuration datastoreConfiguration=new Configuration();

	@Override
	public void start()
	{
		try
		{
			final Injector injector=getInjector();
			final com.kareebo.contacts.server.handler.Configuration handlerConfiguration=new com.kareebo.contacts.server
				                                                                                 .handler.Configuration(injector.getInstance(Key.get(new TypeLiteral<DataStore<Long,User>>()
			{
			})),injector.getInstance(Key.get(new TypeLiteral<DataStore<ByteBuffer,HashIdentity>>()
			{
			})),new ClientNotifier(injector.getInstance(ClientNotifierBackend.class),injector.getInstance(Key.get(new TypeLiteral<DataStore<Long,PendingNotification>>()
			{
			}))),injector.getInstance(GraphAccessor.class));
			final Package classPackage=Verticle.class.getPackage();
			final String packageName=classPackage.getName();
			new ServiceStarter(container,configuration->{
				final Service service;
				try
				{
					service=(Service)Utils.resolveClass(configuration.service,packageName)
						                 .getConstructor(com.kareebo.contacts.server.handler
							                                 .Configuration.class)
						                 .newInstance(handlerConfiguration);
				}
				catch(NoSuchMethodException|ClassNotFoundException|IllegalAccessException|InstantiationException
					      |InvocationTargetException e)
				{
					return e;
				}
				final TProcessor processor=service.create();
				final String address=configuration.address;
				final TEventBusServer eventBusServer=new TEventBusServer(new TEventBusServer.Args(vertx,address).processor(processor));
				eventBusServer.serve();
				final Logger logger=container.logger();
				logger.info("EventBusServer started on address "+address);
				final THttpServer.Args httpArgs=new THttpServer.Args(vertx,configuration.port);
				httpArgs.processor(processor).protocolFactory(new TJSONProtocol.Factory());
				servers.add(new THttpServer(httpArgs));
				return null;
			});
		}
		catch(Throwable throwable)
		{
			container.logger().fatal("Failed to start verticle",throwable);
			return;
		}
		servers.forEach(TServer::serve);
	}

	@Override
	public void stop()
	{
		servers.forEach(TServer::stop);
	}

	@Nonnull
	@Override
	protected AbstractModule provideModule()
	{
		return new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bind(ClientNotifierBackend.class).to(getClientNotifierBackendBinding());
				bind(GraphAccessor.class).to(getGraphAccessorBinding());
			}

			@Provides
			@Singleton
			@Nonnull
			DataStore<Long,User> providesUserDataStore() throws GoraException
			{
				return DataStoreFactory.getDataStore(Long.class,User.class,datastoreConfiguration);
			}

			@Provides
			@Singleton
			@Nonnull
			DataStore<ByteBuffer,HashIdentity> providesIdentityDataStore() throws GoraException
			{
				return DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,datastoreConfiguration);
			}

			@Provides
			@Singleton
			@Nonnull
			DataStore<Long,PendingNotification> providesPendingNotificationDataStore() throws GoraException
			{
				return DataStoreFactory.getDataStore(Long.class,PendingNotification.class,datastoreConfiguration);
			}
		};
	}

	/**
	 * Get the implementation to be bound to {@link ClientNotifierBackend}
	 *
	 * @return An implementation class of @link ClientNotifierBackend}
	 */
	@Nonnull
	abstract protected Class<? extends ClientNotifierBackend> getClientNotifierBackendBinding();

	/**
	 * Get the implementation to be bound to {@link GraphAccessor}
	 *
	 * @return An implementation class of @link GraphAccessor}
	 */
	@Nonnull
	abstract protected Class<? extends GraphAccessor> getGraphAccessorBinding();
}
