package com.kareebo.contacts.client.vertx;

import com.kareebo.contacts.base.vertx.Utils;
import com.kareebo.contacts.client.jobs.*;
import com.kareebo.contacts.client.persistentStorage.PersistentStorage;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

/**
 * Unit test for {@link Verticle}
 */
public class VerticleTest
{
	@Test
	public void test() throws Exception
	{
		final Verticle verticle=new Verticle();
		verticle.setVertx(new Utils.Vertx());
		final String serviceName=Service.class.getName();
		verticle.setContainer(new Utils.Container("{\"services\":[{\"name\":\""+serviceName+"\","+"\"port\":0,"+"\"address\":\"localhost\"}]}"));
		verticle.start();
		verticle.stop();
		if(Utils.Container.lastFatal!=null&&Utils.Container.lastFatalThrowable!=null)
		{
			Utils.Container.lastFatalThrowable.printStackTrace();
		}
		assertNull(Utils.Container.lastFatal);
	}

	@Test
	public void testFails() throws Exception
	{
		final Verticle verticle=new Verticle();
		verticle.setVertx(new Utils.Vertx());
		verticle.setContainer(new Utils.Container("{\"services\":[{\"name\":\"random\","+"\"port\":0,"+
			                                          ""+"\"address\":\"localhost\"}]}"));
		verticle.start();
		assertNotNull(Utils.Container.lastFatal);
		assertNotNull(Utils.Container.lastFatalThrowable);
		assertThat(Utils.Container.lastFatalThrowable,instanceOf(ClassNotFoundException.class));
	}

	@Before
	public void setUp() throws Exception
	{
		Utils.Container.lastFatal=null;
	}

	private static class Verticle extends com.kareebo.contacts.client.vertx.Verticle
	{
		@Nonnull
		@Override
		protected Class<? extends IntermediateResultEnqueuer> getIntermediateResultEnqueuerBinding()
		{
			return Implementations.TestIntermediateResultEnqueuer.class;
		}

		@Nonnull
		@Override
		protected Class<? extends FinalResultEnqueuer> getFinalResultEnqueuerBinding()
		{
			return Implementations.TestFinalResultEnqueuer.class;
		}

		@Nonnull
		@Override
		protected Class<? extends PersistentStorage> getPersistentStorageBinding()
		{
			return Implementations.TestPersistentStorage.class;
		}

		@Nonnull
		@Override
		protected Class<? extends Dequeuer> getDequeuerBinding()
		{
			return TestDequeuer.class;
		}

		@Nonnull
		@Override
		protected Class<? extends FinalResultDispatcher> getFinalResultDispatcher()
		{
			return TestFinalResultDispatcher.class;
		}

		static class TestDequeuer implements Dequeuer
		{
			@Override
			public Job get()
			{
				return null;
			}
		}

		static class TestFinalResultDispatcher implements FinalResultDispatcher
		{
			@Override
			public void dispatch(@Nonnull final ErrorJob errorJob)
			{
			}

			@Override
			public void dispatch(@Nonnull final SuccessJob successJob)
			{
			}
		}
	}

	private static class Service extends com.kareebo.contacts.client.protocol.Service
	{
		protected Service(@Nonnull final Context context,@Nonnull final TAsyncClientManager asyncClientManager,@Nonnull final SigningKey signingKey,@Nonnull final ClientId clientId)
		{
			super(context,asyncClientManager,signingKey,clientId);
		}

		@Override
		protected TAsyncClient construct(@Nonnull final TAsyncClientManager asyncClientManager)
		{
			return null;
		}

		@Nonnull
		@Override
		protected ServiceMethod[] methodNames()
		{
			return new ServiceMethod[0];
		}

		@Nonnull
		@Override
		protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
		{
			return new com.kareebo.contacts.client.jobs.Service.Functor[0];
		}
	}
}