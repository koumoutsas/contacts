package com.kareebo.contacts.client.service;

import com.kareebo.contacts.thrift.NotificationMethod;
import org.apache.thrift.TBase;

/**
 * Connects a {@link AsyncResultHandler<>} with an {@link AsyncResultHandler<TBase>}, triggering the appropriate {@link AsyncResultHandler<TBase>} handlers for success and error. Used by {@link Service#run(NotificationMethod,long,ResultHandler)} implementations
 */
class AsyncResultConnector<T extends TBase> extends AsyncResultHandler<T>
{
	AsyncResultConnector(final ResultHandler<TBase> handler)
	{
		super(new ResultHandler<T>()
		{
			@Override
			public void handleError(final Throwable cause)
			{
				handler.handleError(cause);
			}

			@Override
			public void handle(final T event)
			{
				handler.handle(event);
			}
		});
	}
}
