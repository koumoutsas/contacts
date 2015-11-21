package com.kareebo.contacts.client.handler;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Simple service handler base class
 */
abstract class SimpleServiceHandler
{
	private final String serviceName;
	final private SuccessNotifier onSuccess;
	final private ErrorNotifier onError;

	SimpleServiceHandler(final SuccessNotifier onSuccess,final ErrorNotifier onError)
	{
		this.serviceName=this.getClass().getSimpleName();
		this.onSuccess=onSuccess;
		this.onError=onError;
	}

	/**
	 * Run the service
	 *
	 * @param input The service input
	 */
	public void run(final TBase input)
	{
		final SimpleResultHandler handler=new SimpleResultHandler(serviceName,onSuccess,onError);
		try
		{
			runImplementation(input,handler);
		}
		catch(TException|InvalidKeyException|NoSuchAlgorithmException|NoSuchProviderException|SignatureException e)
		{
			handler.handleError(e);
		}
	}

	abstract protected void runImplementation(final TBase input,final ResultHandler<Void> handler) throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException;
}
