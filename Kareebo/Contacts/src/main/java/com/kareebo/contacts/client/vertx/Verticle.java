package com.kareebo.contacts.client.vertx;

import com.google.inject.*;
import com.kareebo.contacts.base.vertx.ServiceStarter;
import com.kareebo.contacts.client.jobs.*;
import com.kareebo.contacts.client.persistentStorage.PersistedObjectRetriever;
import com.kareebo.contacts.client.persistentStorage.PersistentStorage;
import com.kareebo.contacts.client.protocol.Enqueuers;
import com.kareebo.contacts.client.protocol.ServiceDispatcher;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.persistentStorage.PersistentStorageConstants;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.transport.TClientTransport;
import org.apache.thrift.transport.THttpClientTransport;
import org.apache.thrift.transport.TTransportException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Client verticle starting HTTP endpoints for each service and mapping them with a {@link ServiceDispatcher}. Active services are read from the
 * configuration of Vertx
 */
abstract public class Verticle extends com.kareebo.contacts.base.vertx.Verticle
{
	private final List<TClientTransport> transports=new ArrayList<>();

	@Override
	public void start()
	{
		final Injector injector=getInjector();
		final PersistedObjectRetriever persistedObjectRetriever=new PersistedObjectRetriever(injector.getInstance(PersistentStorage.class));
		final com.kareebo.contacts.thrift.client.SigningKey storedSigningKey=new com.kareebo.contacts.thrift.client.SigningKey();
		try
		{
			persistedObjectRetriever.get(storedSigningKey,PersistentStorageConstants.SigningKey);
			final SigningKey signingKey=new SigningKey(storedSigningKey);
			final ClientId clientId=new ClientId();
			persistedObjectRetriever.get(clientId,PersistentStorageConstants.ClientId);
			final ServiceDispatcher serviceDispatcher=ServiceDispatcherSingletonProvider.get(new Enqueuers(injector.getInstance(IntermediateResultEnqueuer.class),injector.getInstance
				                                                                                                                                                               (FinalResultEnqueuer.class)),signingKey,clientId);
			injector.getInstance(Runner.class).put(JobType.Protocol,new Dispatcher(serviceDispatcher,injector.getInstance
				                                                                                                  (FinalResultDispatcher.class)));
			new ServiceStarter(container,configuration->{
				final THttpClientTransport transport=new THttpClientTransport(new THttpClientTransport.Args(vertx,configuration.port,
					                                                                                           configuration.address));
				try
				{
					transport.open();
					transports.add(transport);
					serviceDispatcher.add(configuration.service,new TAsyncClientManager(transport,new TJSONProtocol.Factory()));
				}
				catch(TTransportException|ClassNotFoundException|ServiceDispatcher.DuplicateService e)
				{
					return e;
				}
				return null;
			});
		}
		catch(Throwable throwable)
		{
			stop();
			container.logger().fatal("Failed to start verticle",throwable);
		}
	}

	@Override
	public void stop()
	{
		transports.forEach(TClientTransport::close);
		ServiceDispatcherSingletonProvider.reset();
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
				bind(IntermediateResultEnqueuer.class).to(getIntermediateResultEnqueuerBinding());
				bind(FinalResultEnqueuer.class).to(getFinalResultEnqueuerBinding());
				bind(PersistentStorage.class).to(getPersistentStorageBinding());
				bind(Dequeuer.class).to(getDequeuerBinding());
				bind(FinalResultDispatcher.class).to(getFinalResultDispatcher());
			}

			@Provides
			@Singleton
			@Nonnull
			Runner provideRunner(@Nonnull final Provider<Dequeuer> dequeuerProvider)
			{
				return new Runner(new HashMap<>(),dequeuerProvider.get());
			}
		};
	}

	/**
	 * Get the implementation to be bound to {@link IntermediateResultEnqueuer}
	 *
	 * @return An implementation class of @link IntermediateResultEnqueuer}
	 */
	@Nonnull
	abstract protected Class<? extends IntermediateResultEnqueuer> getIntermediateResultEnqueuerBinding();

	/**
	 * Get the implementation to be bound to {@link FinalResultEnqueuer}
	 *
	 * @return An implementation class of @link FinalResultEnqueuer}
	 */
	@Nonnull
	abstract protected Class<? extends FinalResultEnqueuer> getFinalResultEnqueuerBinding();

	/**
	 * Get the implementation to be bound to {@link PersistentStorage}
	 *
	 * @return An implementation class of @link PersistentStorage}
	 */
	@Nonnull
	abstract protected Class<? extends PersistentStorage> getPersistentStorageBinding();

	/**
	 * Get a {@link com.kareebo.contacts.client.jobs.Dequeuer} binding to be provided to the constructed {@link Runner}
	 * @return An implementation of {@link com.kareebo.contacts.client.jobs.Dequeuer}
	 */
	@Nonnull
	abstract protected Class<? extends Dequeuer> getDequeuerBinding();

	/**
	 * Get a {@link FinalResultDispatcher} binding to be used to dispatch final jobs from the protocol
	 *
	 * @return An implementation of {@link FinalResultDispatcher}
	 */
	@Nonnull
	abstract protected Class<? extends FinalResultDispatcher> getFinalResultDispatcher();
}
