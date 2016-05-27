package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * A notification payload object with its generation method
 */
class NotificationObject
{
	final TBase object;
	final private ServiceMethod method;
	private ByteBuffer payload;

	NotificationObject(final @Nonnull ServiceMethod method,final @Nonnull TBase object)
	{
		this.method=method;
		this.object=object;
	}

	@Nonnull
	ServiceMethod getMethod()
	{
		return method;
	}

	@Nonnull
	ByteBuffer getPayload() throws TException
	{
		if(payload==null)
		{
			payload=ByteBuffer.wrap(new TSerializer().serialize(object));
			payload.mark();
		}
		return payload;
	}
}
