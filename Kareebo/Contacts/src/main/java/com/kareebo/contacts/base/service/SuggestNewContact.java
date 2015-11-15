package com.kareebo.contacts.base.service;

import com.kareebo.contacts.thrift.NotificationMethod;

/**
 * Utility for providing client and server side service implementations with common method names
 */
public class SuggestNewContact
{
	private static final String serviceName=SuggestNewContact.class.getSimpleName();
	public final static NotificationMethod method0=new NotificationMethod(serviceName,"suggestNewContact0");
	public final static NotificationMethod method2=new NotificationMethod(serviceName,"suggestNewContact2");
}
