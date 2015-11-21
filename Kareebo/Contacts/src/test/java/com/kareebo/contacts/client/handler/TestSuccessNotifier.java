package com.kareebo.contacts.client.handler;

class TestSuccessNotifier implements SuccessNotifier
{
	String service;

	@Override
	public void notify(final String service)
	{
		this.service=service;
	}
}
