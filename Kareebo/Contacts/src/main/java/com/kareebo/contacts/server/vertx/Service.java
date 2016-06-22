package com.kareebo.contacts.server.vertx;

import org.apache.thrift.TProcessor;

import javax.annotation.Nonnull;

/**
 * Interface for services that can be started by {@link Verticle}
 */
interface Service
{
	/**
	 * Factory method for constructing a verticle processor. It constrcuts an {@link org.apache.thrift.TBaseAsyncProcessor} from a Thrift service
	 * AsyncIface class
	 * @return A {@link TProcessor}
	 */
	@Nonnull
	TProcessor create();
}
