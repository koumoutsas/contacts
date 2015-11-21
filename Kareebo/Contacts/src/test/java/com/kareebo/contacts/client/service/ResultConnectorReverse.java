package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.ResultHandler;
import org.apache.thrift.TBase;

class ResultConnectorReverse<T extends TBase> implements ResultHandler<TBase>
{
	final private ResultHandler<T> handler;

	public ResultConnectorReverse(final ResultHandler<T> handler)
	{
		this.handler=handler;
	}

	@Override
	public void handleError(final Throwable cause)
	{
		handler.handleError(cause);
	}

	@Override
	public void handle(final TBase event)
	{
		//noinspection unchecked
		handler.handle((T)event);
	}
}
