package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.NotificationMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class SendContactCard
{
	private static final String serviceName=SendContactCard.class.getSimpleName();
	public final static NotificationMethod method1=new NotificationMethod(serviceName,"sendContactCard1");
	public final static NotificationMethod method3=new NotificationMethod(serviceName,"sendContactCard3");
}
