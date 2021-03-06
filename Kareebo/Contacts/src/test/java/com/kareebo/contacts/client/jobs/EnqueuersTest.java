package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for {@link Enqueuers}
 */
public class EnqueuersTest
{
	final private EnqueuerImplementation enqueuerImplementation=new EnqueuerImplementation();

	@Test
	public void testFinalResultEnqueuer() throws Exception
	{
		assertEquals(enqueuerImplementation,new Enqueuers(new HashMap<>(),enqueuerImplementation).finalResultEnqueuer());
	}

	@Test
	public void testIntermediateResultEnqueuer() throws Exception
	{
		testInternal(new Enqueuers(constructMap(),enqueuerImplementation));
		testInternal(new Enqueuers(JobType.ExternalService,enqueuerImplementation,enqueuerImplementation));
	}

	private void testInternal(final Enqueuers enqueuers)
	{
		assertEquals(enqueuerImplementation,enqueuers.intermediateResultEnqueuer(JobType.ExternalService));
		assertNull(enqueuers.intermediateResultEnqueuer(JobType.Processor));
		assertNull(enqueuers.intermediateResultEnqueuer(JobType.Protocol));
	}

	private Map<JobType,IntermediateResultEnqueuer> constructMap()
	{
		final Map<JobType,IntermediateResultEnqueuer> map=new HashMap<>(2);
		map.put(JobType.ExternalService,enqueuerImplementation);
		map.put(JobType.Processor,null);
		return map;
	}
}