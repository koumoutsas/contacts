package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link IntermediateResultHandler}
 */
public class IntermediateResultHandlerTest
{
	@Test
	public void testHandleSuccess() throws Exception
	{
		final ServiceMethod method=new ServiceMethod("a","b");
		final com.kareebo.contacts.client.processor.ServiceMethod processorMethod=new com.kareebo.contacts.client.processor.ServiceMethod
			                                                                          ("c","d");
		final EnqueuerImplementation enqueuer=new EnqueuerImplementation();
		final IntermediateResultHandler<LongId> resultHandler=new IntermediateResultHandler<>(enqueuer,processorMethod,enqueuer,method,null);
		final LongId expected=new LongId(9);
		resultHandler.handleSuccess(expected);
		assertTrue(enqueuer.hasJob(JobType.Processor,processorMethod,expected));
	}
}
