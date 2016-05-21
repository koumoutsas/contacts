package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.client.protocol.ServiceMethod;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link com.kareebo.contacts.client.protocol.ResultHandler}
 */
public class ResultHandlerTest
{
	@Test
	public void testHandle() throws Exception
	{
		class MyResultHandler extends com.kareebo.contacts.client.jobs.ResultHandler<Void>
		{
			final private FinalResultEnqueuer enqueuer;

			private MyResultHandler(final FinalResultEnqueuer enqueuer,final ServiceMethod method)
			{
				super(enqueuer,method,JobType.Protocol);
				this.enqueuer=enqueuer;
			}

			@Override
			protected void handleSuccess(final Void result)
			{
				enqueuer.success(JobType.Protocol,method.getServiceName(),SuccessCode.Ok);
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
		assertTrue(enqueuer.isSuccess(JobType.Protocol,new ServiceMethod(method.getServiceName(),null),SuccessCode.Ok));
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
		assertTrue(enqueuer.isError(JobType.Protocol,method,ErrorCode.Failure));
	}
}