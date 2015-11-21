package com.kareebo.contacts.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Result handler that bridges service and core
 */
abstract class ResultHandler<E> implements com.kareebo.contacts.client.service.ResultHandler<E>
{
	protected static final Logger logger=LoggerFactory.getLogger(ResultHandler.class.getName());
	protected final String service;
	private final ErrorNotifier errorNotifier;

	/**
	 * Constructor from service name and an {@link ErrorNotifier}
	 *
	 * @param service       The service name
	 * @param errorNotifier The error handler
	 */
	ResultHandler(final String service,final ErrorNotifier errorNotifier)
	{
		this.service=service;
		this.errorNotifier=errorNotifier;
	}

	@Override
	public void handleError(final Throwable cause)
	{
		logger.error("Service "+service+" failed",cause);
		errorNotifier.notify(service,cause);
	}

	@Override
	abstract public void handle(final E event);
}
