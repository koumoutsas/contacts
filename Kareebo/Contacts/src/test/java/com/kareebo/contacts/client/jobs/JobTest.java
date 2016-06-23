package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link Job}
 */
public class JobTest
{
	@Test
	public void getType() throws Exception
	{
		assertEquals(JobType.ExternalService,new Job(JobType.ExternalService)
		{
			@Override
			void dispatch(@Nonnull final Dispatcher dispatcher) throws Service.ExecutionFailed, ServiceDispatcher.NoSuchService, Service.NoSuchMethod
			{
			}
		}.getType());
	}
}