package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;
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
		new Enqueuers((type,method,context,payload)->{
		},new FinalResultEnqueuer()
		{
			@Override
			public void success(@Nonnull final JobType jobType,@Nonnull final String s,final SuccessCode successCode)
			{
			}

			@Override
			public void error(@Nonnull final JobType jobType,final ServiceMethod serviceMethod,@Nonnull final ErrorCode errorCode,@Nonnull final Throwable
				                                                                                                                      cause)
			{
			}
		});
	}
}