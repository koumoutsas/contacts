package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ErrorJob}
 */
public class ErrorJobTest extends DispatcherTestBase
{
	private final EnqueuerImplementation enqueuerImplementation=new EnqueuerImplementation();
	private final FinalResultDispatcher finalResultDispatcher=new FinalResultDispatcher()
	{
		@Override
		public void dispatch(@Nonnull final ErrorJob errorJob)
		{
			enqueuerImplementation.error(errorJob);
		}

		@Override
		public void dispatch(@Nonnull final SuccessJob successJob)
		{
			enqueuerImplementation.success(successJob);
		}
	};
	private final Dispatcher dispatcher=new Dispatcher(new ServiceDispatcherImplementation(enqueuerImplementation),finalResultDispatcher);
	@SuppressWarnings("ThrowableInstanceNeverThrown")
	private final Throwable throwable=new Throwable("a");
	private final ErrorJob job=new ErrorJob(JobType.Protocol,ServiceImplementation.method,ErrorCode.Failure,throwable);

	@Test
	public void getMethod() throws Exception
	{
		assertEquals(ServiceImplementation.method,job.getMethod());
	}

	@Test
	public void getError() throws Exception
	{
		assertEquals(ErrorCode.Failure,job.getError());
	}

	@Test
	public void getCause() throws Exception
	{
		assertEquals(throwable,job.getCause());
	}

	@Test
	public void dispatch() throws Exception
	{
		job.dispatch(dispatcher);
		assertTrue(enqueuerImplementation.isError(job.getType(),job.getMethod(),job.getError()));
	}
}