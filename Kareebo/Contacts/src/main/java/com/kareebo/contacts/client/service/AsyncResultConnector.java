package com.kareebo.contacts.client.service;

import com.kareebo.contacts.thrift.NotificationMethod;
import org.apache.thrift.TBase;

/**
 * Connects a {@link AsyncResultHandler<>} with an {@link AsyncResultHandler<TBase>}, triggering the appropriate {@link AsyncResultHandler<TBase>} handlers for success and error. Used by {@link Service#run(NotificationMethod,long,AsyncResultHandler)} implementations
 */
class AsyncResultConnector<T extends TBase> extends AsyncResultHandler<T>
{
	public AsyncResultConnector(final AsyncResultHandler<TBase> handler)
	{
		super(new ResultHandler<T>()
		{
			@Override
			public void handleError(final Throwable cause)
			{
				handler.handle(new AsyncErrorResult<TBase>(cause));
			}

			@Override
			public void handle(final T event)
			{
				handler.handle(new AsyncSuccessResult<TBase>(event));
			}
		});
	}
}
