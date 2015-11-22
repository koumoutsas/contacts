package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.thrift.ServiceMethod;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link FinalResultHandler}
 */
public class FinalResultHandlerTest
{
	@Test
	public void testHandleSuccess() throws Exception
	{
		final ServiceMethod method=new ServiceMethod("a","b");
		final EnqueuerImplementation enqueuer=new EnqueuerImplementation();
		final FinalResultHandler resultHandler=new FinalResultHandler(enqueuer,method);
		resultHandler.handleSuccess(null);
		assertTrue(enqueuer.job(new ServiceMethod(method.getServiceName(),null),null));
		resultHandler.done();
		assertTrue(enqueuer.job(new ServiceMethod(method.getServiceName(),null),null));
	}
}
