package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.apache.thrift.TBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link IntermediateJob}
 */
public class IntermediateJobTest extends DispatcherTestBase
{
	final private Context context=new Context();
	final private TBase payload=new LongId();
	final private IntermediateJob job=new IntermediateJob(JobType.Protocol,ServiceImplementation.method,context,payload);

	@Test
	public void getMethod() throws Exception
	{
		assertEquals(JobType.Protocol,job.getType());
	}

	@Test
	public void getContext() throws Exception
	{
		assertEquals(context,job.getContext());
	}

	@Test
	public void getPayload() throws Exception
	{
		assertEquals(payload,job.getPayload());
	}

	@Test
	public void dispatch() throws Exception
	{
		job.dispatch(dispatcher);
		assertTrue(enqueuerImplementation.hasJob(job.getType(),job.getMethod(),job.getPayload()));
	}
}