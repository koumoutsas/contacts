package com.kareebo.contacts.server.vertx;

import org.apache.thrift.TProcessor;

import javax.annotation.Nonnull;

/**
 * Interface providing
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
