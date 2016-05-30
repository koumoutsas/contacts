package com.kareebo.contacts.base.vertx;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Read the configuration from a {@link org.vertx.java.platform.Container}, find the services section, and act on them according to a
 * {@link java.util.function.Function}
 */
public class ServiceStarter
{
	/**
	 * Start all services found in the configuration of a {@link Container}
	 *
	 * @param container The {@link Container}
	 * @param actor     The function that starts a service. It returns a {@link Throwable} if an exception has been thrown
	 * @throws Throwable If a service cannot be started
	 */
	public ServiceStarter(final @Nonnull Container container,final @Nonnull Function<ConfigurationSection,Throwable> actor) throws Throwable
	{
		final JsonArray config=container.config().getElement("services").asArray();
		for(final Object aConfig : config)
		{
			final JsonObject serviceConfig=(JsonObject)aConfig;
			final ConfigurationSection configurationSection=new ConfigurationSection(serviceConfig.getString("name"),serviceConfig.getInteger("port")
				                                                                        ,serviceConfig.getString("address"));
			final Throwable error=actor.apply(configurationSection);
			final Logger logger=container.logger();
			if(error!=null)
			{
				logger.fatal("Could not start service",error);
				throw error;
			}
			logger.info("Service "+configurationSection.service+" started at "+configurationSection.address+":"+configurationSection.port);
		}
	}

	/**
	 * Encapsulation of the service configuration to be used for the {@link java.util.function.Function}
	 */
	@SuppressWarnings("WeakerAccess")
	public class ConfigurationSection
	{
		final public String service;
		final public int port;
		final public String address;

		private ConfigurationSection(final @Nonnull String service,final int port,final @Nonnull String address)
		{
			this.service=service;
			this.port=port;
			this.address=address;
		}
	}
}
