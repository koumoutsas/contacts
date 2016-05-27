package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.client.jobs.Notification;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link ClientNotifierBackend} for tests
 */
class Notifier implements ClientNotifierBackend
{
	final private Map<Long,byte[]> sentNotifications=new HashMap<>();
	boolean fail=false;

	@Override
	public void notify(final long deviceToken,@Nonnull final byte[] payload) throws FailedOperation
	{
		if(fail)
		{
			throw new FailedOperation();
		}
		sentNotifications.put(deviceToken,payload);
	}

	Notification getFirst() throws TException
	{
		final Notification ret=new Notification();
		new TDeserializer().deserialize(ret,sentNotifications.values().iterator().next());
		return ret;
	}

	Notification get(final Long deviceToken) throws TException
	{
		final Notification ret=new Notification();
		new TDeserializer().deserialize(ret,sentNotifications.get(deviceToken));
		return ret;
	}

	int size()
	{
		return sentNotifications.size();
	}

	void put(final Long deviceToken,final Notification notification) throws TException
	{
		sentNotifications.put(deviceToken,new TSerializer().serialize(notification));
	}
}

