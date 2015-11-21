package com.kareebo.contacts.client.handler;

import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Handler for the modify user agent service
 */
public class ModifyUserAgent extends SimpleServiceHandler
{
	final private com.kareebo.contacts.client.service.ModifyUserAgent service;

	public ModifyUserAgent(final com.kareebo.contacts.client.service.ModifyUserAgent service,final SuccessNotifier onSuccess,final ErrorNotifier onError)
	{
		super(onSuccess,onError);
		this.service=service;
	}

	@Override
	protected void runImplementation(final TBase input,final ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		service.modifyUserAgent1((UserAgent)input,handler);
	}
}
