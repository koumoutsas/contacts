package com.kareebo.contacts.client.handler;

import com.kareebo.contacts.thrift.HashBufferSet;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Handler for the register unconfirmed identity service
 */
public class RegisterUnconfirmedIdentity extends SimpleServiceHandler
{
	final private com.kareebo.contacts.client.service.RegisterUnconfirmedIdentity service;

	RegisterUnconfirmedIdentity(final com.kareebo.contacts.client.service.RegisterUnconfirmedIdentity service,final SuccessNotifier onSuccess,final ErrorNotifier onError)
	{
		super(onSuccess,onError);
		this.service=service;
	}

	@Override
	protected void runImplementation(final TBase input,final ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		service.registerUnconfirmedIdentity1((HashBufferSet)input,handler);
	}
}
