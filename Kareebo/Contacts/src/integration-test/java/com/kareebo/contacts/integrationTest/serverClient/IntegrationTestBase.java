package com.kareebo.contacts.integrationTest.serverClient;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.base.vertx.Utils;
import com.kareebo.contacts.client.jobs.*;
import com.kareebo.contacts.client.persistentStorage.PersistedObjectRetriever;
import com.kareebo.contacts.client.persistentStorage.PersistentStorage;
import com.kareebo.contacts.client.protocol.ServiceDispatcher;
import com.kareebo.contacts.crypto.TestSignatureKeyPair;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.server.handler.ClientNotifierBackend;
import com.kareebo.contacts.server.handler.GraphAccessor;
import com.kareebo.contacts.server.vertx.Verticle;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.client.persistentStorage.PersistentStorageConstants;
import org.apache.gora.store.DataStore;
import org.apache.gora.util.GoraException;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.transport.TTransportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

import javax.annotation.Nonnull;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * Base class for all integration tests
 */
abstract class IntegrationTestBase
{
	private static final String host="localhost";
	private com.kareebo.contacts.client.vertx.Verticle client;
	private Injector clientInjector;
	private JobQueue jobQueue;
	private Verticle server;
	private Injector serverInjector;
	private DataStore<Long,User> dataStore;
	private PersistedObjectRetriever persistedObjectRetriever;

	@Test
	public void testIdempotent() throws Exception
	{
		assumeTrue(isIdempotent());
		testMethod();
		test();
	}

	protected abstract boolean isIdempotent();

	abstract protected void testMethod() throws Exception;

	@Test
	public void test() throws Exception
	{
		testMethod();
		await().atMost(5,TimeUnit.SECONDS).until(()->jobQueue.done);
		assertNull(jobQueue.error);
		checkDatastore();
	}

	abstract protected void checkDatastore() throws TException, PersistentStorage.NoSuchKey;

	@Nonnull
	abstract protected String serviceName();

	@Before
	public void setUp() throws Exception
	{
		final ServerSocket socket=new ServerSocket(0);
		final int port=socket.getLocalPort();
		socket.close();
		final Vertx vertx=VertxFactory.newVertx();
		prepareClient(vertx,port);
		prepareServer(vertx,port);
	}

	private void prepareServer(final Vertx vertx,final int port) throws PersistentStorage.NoSuchKey, TException, GoraException
	{
		server=new TestVerticle();
		serverInjector=server.getInjector();
		prepareDatastore();
		prepareVerticle(vertx,port,server);
	}

	private void prepareDatastore() throws GoraException, TException, PersistentStorage.NoSuchKey
	{
		dataStore=serverInjector.getInstance(Key.get(new TypeLiteral<DataStore<Long,User>>()
		{
		}));
		final ClientId clientId=getClientId();
		final User user=new User();
		final HashMap<CharSequence,Client> clients=new HashMap<>();
		final Client client=new Client();
		final com.kareebo.contacts.server.gora.UserAgent userAgent=new com.kareebo.contacts.server.gora.UserAgent();
		userAgent.setPlatform("1");
		userAgent.setVersion("1");
		client.setUserAgent(userAgent);
		final PublicKeys publicKeys=new PublicKeys();
		final byte[] bufferBytes={'a','b'};
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bufferBytes);
		byteBuffer.mark();
		final EncryptionKey encryptionKey=new EncryptionKey();
		encryptionKey.setAlgorithm(EncryptionAlgorithm.RSA2048);
		encryptionKey.setBuffer(byteBuffer);
		publicKeys.setEncryption(encryptionKey);
		publicKeys.setVerification(((TestPersistentStorage)clientInjector.getInstance(PersistentStorage.class)).getVerificationKey());
		client.setKeys(publicKeys);
		client.setComparisonIdentities(new ArrayList<>());
		clients.put(TypeConverter.convert(clientId.getClient()),client);
		user.setClients(clients);
		user.setBlind(byteBuffer);
		user.setIdentities(new ArrayList<>());
		user.setSentRequests(new ArrayList<>());
		dataStore.put(clientId.getUser(),user);
	}

	private void prepareClient(final Vertx vertx,final int port) throws TTransportException, ServiceDispatcher.DuplicateService,
		                                                                    ClassNotFoundException,
		                                                                    InvalidKeySpecException, NoSuchAlgorithmException
	{
		client=new com.kareebo.contacts.client.vertx.Verticle()
		{
			@Nonnull
			@Override
			protected Class<? extends IntermediateResultEnqueuer> getIntermediateResultEnqueuerBinding()
			{
				return JobQueue.class;
			}

			@Nonnull
			@Override
			protected Class<? extends FinalResultEnqueuer> getFinalResultEnqueuerBinding()
			{
				return JobQueue.class;
			}

			@Nonnull
			@Override
			protected Class<? extends PersistentStorage> getPersistentStorageBinding()
			{
				return TestPersistentStorage.class;
			}

			@Nonnull
			@Override
			protected Class<? extends Dequeuer> getDequeuerBinding()
			{
				return JobQueue.class;
			}

			@Nonnull
			@Override
			protected Class<? extends FinalResultDispatcher> getFinalResultDispatcher()
			{
				return JobQueue.class;
			}
		};
		clientInjector=client.getInjector();
		jobQueue=(JobQueue)clientInjector.getInstance(IntermediateResultEnqueuer.class);
		final Runner runner=clientInjector.getInstance(Runner.class);
		jobQueue.createDispatchers(clientInjector).forEach(runner::put);
		jobQueue.setRunner(runner);
		persistedObjectRetriever=new PersistedObjectRetriever(clientInjector.getInstance(PersistentStorage.class));
		prepareVerticle(vertx,port,client);
	}

	private void prepareVerticle(final Vertx vertx,final int port,final org.vertx.java.platform.Verticle verticle)
	{
		verticle.setVertx(vertx);
		verticle.setContainer(new Utils.Container("{\"services\":[{\"name\":\""+serviceName()+"\",\"port\":"+port+","
			                                          +"\"address\":\""+host+"\"}]}"));
		verticle.start();
		if(Utils.Container.lastFatal!=null)
		{
			if(Utils.Container.lastFatalThrowable!=null)
			{
				Utils.Container.lastFatalThrowable.printStackTrace();
			}
			fail(Utils.Container.lastFatal.toString());
		}
	}

	@After
	public void tearDown() throws Exception
	{
		Utils.Container.lastFatal=null;
		if(server!=null)
		{
			server.stop();
		}
		if(client!=null)
		{
			client.stop();
		}
		server=null;
		client=null;
	}

	@Nonnull
	Client getClient() throws TException, PersistentStorage.NoSuchKey
	{
		final ClientId clientId=getClientId();
		return dataStore.get(clientId.getUser()).getClients().get(TypeConverter.convert(clientId.getClient
			                                                                                         ()));
	}

	private ClientId getClientId() throws TException, PersistentStorage.NoSuchKey
	{
		final ClientId clientId=new ClientId();
		persistedObjectRetriever.get(clientId,PersistentStorageConstants.ClientId);
		return clientId;
	}

	private static class TestClientNotifierBackend implements ClientNotifierBackend
	{
		@Override
		public void notify(final long deviceToken,@Nonnull final byte[] payload) throws FailedOperation
		{
		}
	}

	private static class TestGraphAccessor implements GraphAccessor
	{
		@Override
		public void addEdges(final Long from,@Nonnull final HashSet<Long> to)
		{
		}

		@Override
		public void removeEdges(@Nonnull final Long from,@Nonnull final HashSet<Long> to) throws IllegalStateException
		{
		}

		@Override
		public void close()
		{
		}
	}

	private static class TestVerticle extends Verticle
	{
		@Nonnull
		@Override
		protected Class<? extends ClientNotifierBackend> getClientNotifierBackendBinding()
		{
			return TestClientNotifierBackend.class;
		}

		@Nonnull
		@Override
		protected Class<? extends GraphAccessor> getGraphAccessorBinding()
		{
			return TestGraphAccessor.class;
		}
	}

	@Singleton
	private static class TestPersistentStorage implements PersistentStorage
	{
		private final Map<String,byte[]> cache=new HashMap<>(2);
		private final TestSignatureKeyPair keyPair=new TestSignatureKeyPair();

		private TestPersistentStorage() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
		{
		}

		@Nonnull
		@Override
		public byte[] get(@Nonnull final String key) throws NoSuchKey
		{
			switch(key)
			{
				case PersistentStorageConstants.SigningKey:
					try
					{
						if(!cache.containsKey(PersistentStorageConstants.SigningKey))
						{
							cache.put(key,getSigningKey());
						}
						return cache.get(key);
					}
					catch(TException e)
					{
						return new byte[0];
					}
				case PersistentStorageConstants.ClientId:
					try
					{
						if(!cache.containsKey(PersistentStorageConstants.ClientId))
						{
							cache.put(key,new TSerializer().serialize(new ClientId(0,0)));
						}
						return cache.get(key);
					}
					catch(TException e)
					{
						return new byte[0];
					}
				default:
					throw new NoSuchKey();
			}
		}

		@Nonnull
		private byte[] getSigningKey() throws TException
		{
			final ByteBuffer buffer=ByteBuffer.wrap(new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()).getEncoded());
			buffer.mark();
			return new TSerializer().serialize(new com.kareebo.contacts.thrift.client.SigningKey(buffer,SignatureAlgorithm.SHA512withECDSAprime239v1));
		}

		@Override
		public void put(@Nonnull final String key,@Nonnull final byte[] value)
		{
		}

		@Override
		public void remove(@Nonnull final String key) throws NoSuchKey
		{
		}

		@Override
		public void start()
		{
		}

		@Override
		public void commit()
		{
		}

		@Override
		public void rollback()
		{
		}

		@Nonnull
		VerificationKey getVerificationKey()
		{
			return keyPair.verificationKey();
		}
	}
}
