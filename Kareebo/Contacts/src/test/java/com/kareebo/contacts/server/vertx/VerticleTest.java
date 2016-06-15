package com.kareebo.contacts.server.vertx;

import com.kareebo.contacts.base.vertx.Utils;
import com.kareebo.contacts.server.handler.ClientNotifierBackend;
import com.kareebo.contacts.server.handler.Configuration;
import com.kareebo.contacts.server.handler.GraphAccessor;
import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Unit test for {@link Verticle}
 */
public class VerticleTest
{
	@Test
	public void test() throws Exception
	{
		final Verticle verticle=new TestVerticle();
		verticle.setContainer(new Utils.Container("{\"services\":[{\"name\":\""+VerticleTest.class.getSimpleName()+"$"+Service.class
			                                                                                                               .getSimpleName()+"\","+
			                                          "\"port\":0,"+"\"address\":\"localhost\"}]}"));
		final Utils.Vertx vertx=new Utils.Vertx();
		verticle.setVertx(vertx);
		verticle.start();
		assertTrue(vertx.handlerRegistered);
		assertTrue(vertx.serverWorking);
		verticle.stop();
		assertFalse(vertx.serverWorking);
	}

	@Test
	public void testServiceThrows() throws Exception
	{
		final Verticle verticle=new TestVerticle();
		verticle.setContainer(new Utils.Container("{\"services\":[{\"name\":\"blah\",\"port\":0,\"address\":\"\"}]}"));
		verticle.setVertx(new Utils.Vertx());
		verticle.start();
		assertNotNull(Utils.Container.lastFatal);
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

	private static class Service implements com.kareebo.contacts.server.vertx.Service
	{
		public Service(@SuppressWarnings("UnusedParameters") final Configuration configuration)
		{
		}

		@Nonnull
		@Override
		public TProcessor create()
		{
			return new TProcessor()
			{
				@Override
				public boolean process(final TProtocol tProtocol,final TProtocol tProtocol1) throws TException
				{
					return false;
				}

				@Override
				public boolean isAsyncProcessor()
				{
					return false;
				}
			};
		}
	}
}