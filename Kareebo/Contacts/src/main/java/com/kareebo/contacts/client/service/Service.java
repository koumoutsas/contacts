package com.kareebo.contacts.client.service;

import com.kareebo.contacts.thrift.NotificationMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * An interface for all services provided by the client
 */
interface Service
{
	/**
	 * Run a method based on its name and a notification id
	 *
	 * @param method         The method to be called
	 * @param notificationId The notification id
	 * @param handler        The asynchronous handler for the result
	 * @throws NoSuchMethod             When the method cannot be found
	 * @throws NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException When The execution of the method failed for security or payload deserialization reasons
	 */
	void run(NotificationMethod method,long notificationId,AsyncResultHandler<TBase> handler) throws NoSuchMethod, NoSuchProviderException, TException, NoSuchAlgorithmException, InvalidKeyException, SignatureException;

	/// Exception thrown when a method cannot be found
	class NoSuchMethod extends Exception
	{
	}
}
