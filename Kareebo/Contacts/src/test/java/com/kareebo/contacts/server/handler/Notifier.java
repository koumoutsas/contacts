package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.thrift.FailedOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link ClientNotifierBackend} for tests
 */
class Notifier implements ClientNotifierBackend
{
	final Map<Long,Long> sentNotifications=new HashMap<>();
	boolean fail=false;

	@Override
	public void notify(final long deviceToken,final long notificationId) throws FailedOperation
	{
		if(fail)
		{
			throw new FailedOperation();
		}
		sentNotifications.put(deviceToken,notificationId);
	}
}

