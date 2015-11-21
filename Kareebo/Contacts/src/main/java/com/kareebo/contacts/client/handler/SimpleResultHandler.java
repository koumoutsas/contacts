package com.kareebo.contacts.client.handler;

/**
 * Simple implementation of {@link ResultHandler<Void>}
 */
class SimpleResultHandler extends ResultHandler<Void>
{
	private final SuccessNotifier successNotifier;

	/**
	 * Constructor from service name and an {@link ErrorNotifier}
	 *
	 * @param service       The service name
	 * @param errorNotifier The error handler
	 */
	SimpleResultHandler(final String service,final SuccessNotifier successNotifier,final ErrorNotifier errorNotifier)
	{
		super(service,errorNotifier);
		this.successNotifier=successNotifier;
	}

	@Override
	public void handle(final Void event)
	{
		logger.info("Service "+service+" completed");
		successNotifier.notify(service);
	}
}
