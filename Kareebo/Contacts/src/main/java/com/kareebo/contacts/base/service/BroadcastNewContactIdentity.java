package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.NotificationMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class BroadcastNewContactIdentity
{
	private static final String serviceName=BroadcastNewContactIdentity.class.getSimpleName();
	public final static NotificationMethod method3=new NotificationMethod(serviceName,"broadcastNewContactIdentity3");
}
