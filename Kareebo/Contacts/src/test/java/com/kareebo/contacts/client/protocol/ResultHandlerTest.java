package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.EnqueuerImplementation;
import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import org.junit.Test;

import javax.annotation.Nonnull;

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
			private ResultHandlerImplementation(final ErrorEnqueuer errorEnqueuer,final ServiceMethod method)
			{
				super(errorEnqueuer,method);
			}

			@Override
			protected void handleSuccess(@Nonnull final Long result)
			{
			}
		}
		new ResultHandlerImplementation(new EnqueuerImplementation(),new ServiceMethod("a","b"));
	}
}