package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.ServiceMethod;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ResultHandler}
 */
public class ResultHandlerTest
{
	@Test
	public void testHandle() throws Exception
	{
		class MyResultHandler extends ResultHandler<Void>
		{
			MyResultHandler(final Enqueuer enqueuer,final ServiceMethod method)
			{
				super(enqueuer,method);
			}

			@Override
			void handleSuccess(final Void result)
			{
				enqueuer.success(method.getServiceName());
			}
		}
		final ServiceMethod method=new ServiceMethod("a","b");
		final EnqueuerImplementation enqueuer=new EnqueuerImplementation();
		final MyResultHandler resultHandler=new MyResultHandler(enqueuer,method);
		resultHandler.handle(new AsyncResult<Void>()
		{
			@Override
			public Void result()
			{
				return null;
			}

			@Override
			public Throwable cause()
			{
				return null;
			}

			@Override
			public boolean succeeded()
			{
				return true;
			}

			@Override
			public boolean failed()
			{
				return false;
			}
		});
		assertTrue(enqueuer.job(new ServiceMethod(method.getServiceName(),null),null));
		resultHandler.handle(new AsyncResult<Void>()
		{
			@Override
			public Void result()
			{
				return null;
			}

			@Override
			public Throwable cause()
			{
				return new FailedOperation();
			}

			@Override
			public boolean succeeded()
			{
				return false;
			}

			@Override
			public boolean failed()
			{
				return true;
			}
		});
		assertTrue(enqueuer.error(method,new FailedOperation()));
	}
}