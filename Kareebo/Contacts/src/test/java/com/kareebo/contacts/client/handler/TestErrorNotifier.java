package com.kareebo.contacts.client.handler;

class TestErrorNotifier implements ErrorNotifier
{
	String service;
	Throwable cause;

	@Override
	public void notify(final String service,final Throwable cause)
	{
		this.service=service;
		this.cause=cause;
	}
}
