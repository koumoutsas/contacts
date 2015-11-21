package com.kareebo.contacts.client.service;

import com.kareebo.contacts.thrift.LongId;
import org.apache.thrift.TBase;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link AsyncResultConnector}
 */
public class AsyncResultConnectorTest
{
	@Test
	public void test()
	{
		class Status
		{
			LongId result;
			Throwable error;
		}
		final Status expectedStatus=new Status();
		expectedStatus.result=new LongId();
		expectedStatus.error=new Exception();
		final Status status=new Status();
		final ResultHandler<TBase> handler=new ResultHandler<TBase>()
		{
			@Override
			public void handleError(final Throwable cause)
			{
				status.result=null;
				status.error=cause;
			}

			@Override
			public void handle(final TBase event)
			{
				status.result=(LongId)event;
				status.error=null;
			}
		};
		final AsyncResultConnector<LongId> connector=new AsyncResultConnector<>(handler);
		connector.handle(new AsyncSuccessResult<>(expectedStatus.result));
		assertTrue(expectedStatus.result==status.result);
		assertNull(status.error);
		connector.handle(new AsyncErrorResult<LongId>(expectedStatus.error));
		assertNull(status.result);
		assertTrue(expectedStatus.error==status.error);
	}

	/**
	 * Implementation of {@link AsyncResult} that returns only error
	 */
	static class AsyncErrorResult<T> implements AsyncResult<T>
	{
		final private Throwable cause;

		AsyncErrorResult(final Throwable cause)
		{
			this.cause=cause;
		}

		@Override
		public T result()
		{
			return null;
		}

		@Override
		public Throwable cause()
		{
			return cause;
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
	}
}