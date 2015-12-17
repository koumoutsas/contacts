package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.ErrorEnqueuer;
import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.vertx.java.core.AsyncResult;

/**
 * Implementation of {@link org.vertx.java.core.AsyncResultHandler} using {@link ErrorEnqueuer}
 */
abstract class ResultHandler<T> extends com.kareebo.contacts.thrift.client.jobs.ResultHandler<T>
{
	ResultHandler(final ErrorEnqueuer errorEnqueuer,final ServiceMethod method)
	{
		super(errorEnqueuer,method,JobType.Processor);
	}
}
