package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.JobType;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link org.vertx.java.core.AsyncResultHandler} using {@link ErrorEnqueuer}
 */
abstract class ResultHandler<T> extends com.kareebo.contacts.client.jobs.ResultHandler<T>
{
	protected ResultHandler(@Nonnull final ErrorEnqueuer errorEnqueuer,@Nonnull final ServiceMethod method)
	{
		super(errorEnqueuer,method,JobType.Protocol);
	}
}
