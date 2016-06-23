package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Dispatcher}
 */
public class DispatcherTest extends DispatcherTestBase
{
	@Test
	public void dispatch() throws Exception
	{
		final IntermediateJob job=new IntermediateJob(JobType.Protocol,ServiceImplementation.method,new Context(),new LongId());
		dispatcher.dispatch(job);
		assertTrue(enqueuerImplementation.hasJob(job.getType(),job.getMethod(),job.getPayload()));
	}

	@Test
	public void dispatch1() throws Exception
	{
		final ErrorJob job=new ErrorJob(JobType.Protocol,ServiceImplementation.method,ErrorCode.Failure,new Throwable("5"));
		dispatcher.dispatch(job);
		assertTrue(enqueuerImplementation.isError(job.getType(),job.getMethod(),job.getError()));
	}

	@Test
	public void dispatch2() throws Exception
	{
		final SuccessJob job=new SuccessJob(JobType.Protocol,ServiceImplementation.method.getServiceName(),SuccessCode.Ok);
		dispatcher.dispatch(job);
		assertTrue(enqueuerImplementation.isSuccess(job.getType(),new ServiceMethod(job.getService(),null),job.getResult()));
	}
}