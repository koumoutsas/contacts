package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link SuccessJob}
 */
public class SuccessJobTest extends DispatcherTestBase
{
	private final SuccessJob job=new SuccessJob(JobType.Protocol,ServiceImplementation.method.getServiceName(),SuccessCode.Ok);

	@Test
	public void getService() throws Exception
	{
		assertEquals(ServiceImplementation.method.getServiceName(),job.getService());
	}

	@Test
	public void getResult() throws Exception
	{
		assertEquals(SuccessCode.Ok,job.getResult());
	}

	@Test
	public void dispatch() throws Exception
	{
		job.dispatch(dispatcher);
		assertTrue(enqueuerImplementation.isSuccess(job.getType(),new ServiceMethod(job.getService(),null),job.getResult()));
	}
}