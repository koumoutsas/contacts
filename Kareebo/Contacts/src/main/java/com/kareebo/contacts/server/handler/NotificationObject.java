package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.thrift.NotificationMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.nio.ByteBuffer;

/**
 * A notification payload object with its generation method
 */
class NotificationObject
{
	final TBase object;
	final private NotificationMethod method;
	ByteBuffer payload;

	NotificationObject(final NotificationMethod method,final TBase object)
	{
		this.method=method;
		this.object=object;
	}

	NotificationMethod getMethod()
	{
		return method;
	}

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
