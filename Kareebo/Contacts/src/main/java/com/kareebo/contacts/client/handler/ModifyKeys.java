package com.kareebo.contacts.client.handler;

import com.kareebo.contacts.thrift.PublicKeys;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Handler for the modify keys service
 */
public class ModifyKeys extends SimpleServiceHandler
{
	final private com.kareebo.contacts.client.service.ModifyKeys service;

	public ModifyKeys(final com.kareebo.contacts.client.service.ModifyKeys service,final SuccessNotifier onSuccess,final ErrorNotifier onError)
	{
		super(onSuccess,onError);
		this.service=service;
	}

	@Override
	protected void runImplementation(final TBase input,final ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		service.modifyKeys1((PublicKeys)input,handler);
	}
}
