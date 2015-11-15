package com.kareebo.contacts.client.service;

import org.apache.thrift.TBase;

/**
 * Reverse {@link AsyncResultConnector}
 */
class AsyncResultConnectorReverse<T extends TBase> extends AsyncResultHandler<TBase>
{
	public AsyncResultConnectorReverse(final AsyncResultHandler<T> handler)
	{
		super(new ResultHandler<TBase>()
		{
			@Override
			public void handle(final TBase event)
			{
				//noinspection unchecked
				handler.handle(new AsyncSuccessResult<>((T)event));
			}

			@Override
			public void handleError(final Throwable cause)
			{
				handler.handle(new AsyncErrorResult<T>(cause));
			}
		});
	}
}
