package com.kareebo.contacts.server.vertx;

import org.apache.thrift.TProcessor;

import javax.annotation.Nonnull;

/**
 * Interface for services that can be started by {@link Verticle}
 */
interface Service
{
	/**
	 * Factory method for constructing a verticle processor
	 *
	 * @return A {@link TProcessor}
	 */
	@Nonnull
	TProcessor create();
}
