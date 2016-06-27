package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.Context;
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
		final com.kareebo.contacts.client.protocol.ServiceMethod protocolMethod=new com.kareebo.contacts.client.protocol.ServiceMethod("a",
			                                                                                                                              "b");
		final ServiceMethod method=new ServiceMethod
			                           ("c","d");
		final EnqueuerImplementation enqueuer=new EnqueuerImplementation();
		final IntermediateResultHandler<LongId> resultHandler=new IntermediateResultHandler<>(enqueuer,protocolMethod,enqueuer,method,new Context());
		final LongId expected=new LongId(9);
		resultHandler.handleSuccess(expected);
		assertTrue(enqueuer.hasJob(JobType.Protocol,protocolMethod,expected));
	}
}