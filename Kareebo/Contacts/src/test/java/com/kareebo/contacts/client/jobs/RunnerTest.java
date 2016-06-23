package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Runner}
 */
public class RunnerTest extends DispatcherTestBase
{
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void run() throws Exception
	{
		final IntermediateJob job=new IntermediateJob(JobType.Protocol,ServiceImplementation.method,new Context(),new LongId());
		final Dequeuer dequeuer=()->job;
		new Runner(Collections.singletonMap(JobType.Protocol,dispatcher),dequeuer).run();
		assertTrue(enqueuerImplementation.hasJob(job.getType(),job.getMethod(),job.getPayload()));
	}

	@Test
	public void runNoJob() throws Exception
	{
		enqueuerImplementation.reset();
		final Dequeuer dequeuer=()->null;
		new Runner(Collections.singletonMap(JobType.Protocol,dispatcher),dequeuer).run();
		assertTrue(enqueuerImplementation.initialState());
	}

	@Test
	public void runNoDispatcher() throws Exception
	{
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("No service dispatcher for job type "+JobType.Protocol);
		new Runner(Collections.singletonMap(JobType.Processor,dispatcher),()->new IntermediateJob(JobType.Protocol,ServiceImplementation.method,new Context(),new LongId())).run();
	}

	@Test
	public void runError() throws Exception
	{
		final Service.NoSuchMethod cause=new Service.NoSuchMethod();
		try
		{
			new Runner(Collections.singletonMap(JobType.Protocol,new Dispatcher(new ServiceDispatcherImplementation
				                                                                    (enqueuerImplementation,cause),
				                                                                   finalResultDispatcher)),()->new IntermediateJob(JobType.Protocol,ServiceImplementation.method,new Context(),new LongId())).run();
		}
		catch(RuntimeException e)
		{
			assertEquals(cause,e.getCause());
		}
	}
}