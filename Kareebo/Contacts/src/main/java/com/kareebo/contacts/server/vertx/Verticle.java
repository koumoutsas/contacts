package com.kareebo.contacts.server.vertx;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.TEventBusServer;
import org.apache.thrift.server.THttpServer;
import org.apache.thrift.server.TServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import javax.annotation.Nonnull;

/**
 * Verticle extension that launches a specific service
 */
class Verticle extends org.vertx.java.platform.Verticle
{
	final private Service service;
	private TServer server;

	Verticle(final @Nonnull Service service)
	{
		this.service=service;
	}

	@Override
	public void start()
	{
		final TProcessor processor=service.create();
		final String serviceName=service.getClass().getSimpleName();
		final JsonObject config=container.config().getElement(serviceName).asObject();
		final String address=config.getString("address");
		final int port=config.getInteger("port");
		final TEventBusServer eventBusServer=new TEventBusServer(new TEventBusServer.Args(vertx,address).processor(processor));
		eventBusServer.serve();
		final Logger logger=container.logger();
		logger.info("EventBusServer started on address "+address);
		final THttpServer.Args httpArgs=new THttpServer.Args(vertx,port);
		httpArgs.processor(processor).protocolFactory(new TJSONProtocol.Factory());
		server=new THttpServer(httpArgs);
		server.serve();
		container.logger().info("Server listening on port "+port+" for service "+serviceName);
	}

	@Override
	public void stop()
	{
		if(server!=null)
		{
			server.stop();
		}
	}
}
