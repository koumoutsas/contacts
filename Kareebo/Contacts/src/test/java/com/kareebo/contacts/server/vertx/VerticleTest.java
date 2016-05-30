package com.kareebo.contacts.server.vertx;

import com.kareebo.contacts.base.vertx.Utils;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.junit.Test;

import javax.annotation.Nonnull;

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
		final Verticle verticle=new Verticle();
		verticle.setContainer(new Utils.Container("{\"services\":[{\"name\":\"blah\",\"port\":0,\"address\":\"\"}]}"));
		verticle.setVertx(new Utils.Vertx());
		verticle.start();
		assertNotNull(Utils.Container.lastFatal);
	}

	private static class Service implements com.kareebo.contacts.server.vertx.Service
	{
		public Service()
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