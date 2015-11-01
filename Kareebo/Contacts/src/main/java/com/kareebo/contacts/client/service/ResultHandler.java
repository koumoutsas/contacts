package com.kareebo.contacts.client.service;

import org.vertx.java.core.Handler;

/**
 * Extension of {@link Handler} with an error handling method
 */
interface ResultHandler<E> extends Handler<E>
{
	/**
	 * An error has happened
	 *
	 * @param cause The error cause
	 */
	void handleError(final Throwable cause);
}
