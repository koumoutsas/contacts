package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.ErrorJob;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.SuccessJob;
import org.junit.Test;

import javax.annotation.Nonnull;

/**
 * Unit test for {@link Enqueuers}
 */
public class EnqueuersTest
{
	@Test
	public void test() throws Exception
	{
		new Enqueuers((job)->{
		},new FinalResultEnqueuer()
		{
			@Override
			public void error(@Nonnull final ErrorJob job)
			{
			}

			@Override
			public void success(@Nonnull final SuccessJob job)
			{
			}
		});
	}
}