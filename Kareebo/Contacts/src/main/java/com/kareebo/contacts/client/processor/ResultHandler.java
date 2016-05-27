package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.JobType;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link org.vertx.java.core.AsyncResultHandler} using {@link ErrorEnqueuer}
 */
abstract class ResultHandler<T> extends com.kareebo.contacts.client.jobs.ResultHandler<T>
{
	protected ResultHandler(final @Nonnull ErrorEnqueuer errorEnqueuer,final @Nonnull ServiceMethod method)
	{
		super(errorEnqueuer,method,JobType.Processor);
	}
}
