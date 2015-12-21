package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import org.junit.Test;

/**
 * Unit test for {@link ResultHandler}
 */
public class ResultHandlerTest
{
	@Test
	public void test()
	{
		class ResultHandlerImplementation extends ResultHandler<Long>
		{
			protected ResultHandlerImplementation(final ErrorEnqueuer errorEnqueuer,final ServiceMethod method)
			{
				super(errorEnqueuer,method);
			}

			@Override
			protected void handleSuccess(final Long result)
			{
			}
		}
		new ResultHandlerImplementation(new EnqueuerImplementation(),new ServiceMethod("a","b"));
	}
}