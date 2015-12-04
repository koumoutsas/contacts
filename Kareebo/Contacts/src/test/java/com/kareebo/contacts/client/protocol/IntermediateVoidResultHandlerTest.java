package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link IntermediateVoidResultHandler}
 */
public class IntermediateVoidResultHandlerTest
{
	@Test
	public void testHandleSuccess() throws Exception
	{
		final ServiceMethod method=new ServiceMethod("a","b");
		final EnqueuerImplementation enqueuer=new EnqueuerImplementation();
		final IntermediateVoidResultHandler resultHandler=new IntermediateVoidResultHandler(enqueuer,method);
		resultHandler.handleSuccess(null);
		assertTrue(enqueuer.initialState());
	}
}
