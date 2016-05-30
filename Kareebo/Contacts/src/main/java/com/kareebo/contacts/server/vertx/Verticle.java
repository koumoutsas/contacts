package com.kareebo.contacts.server.vertx;

import com.kareebo.contacts.base.vertx.ServiceStarter;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.TEventBusServer;
import org.apache.thrift.server.THttpServer;
import org.apache.thrift.server.TServer;
import org.vertx.java.core.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Verticle extension that launches a specific service
 */
class Verticle extends org.vertx.java.platform.Verticle
{
	private final List<TServer> servers=new ArrayList<>();

	@Override
	public void start()
	{
		try
		{
			new ServiceStarter(container,configuration->{
				final Service service;
				try
				{
					service=(Service)Class.forName(this.getClass().getPackage().getName()+"."+configuration.service).getConstructor()
						                 .newInstance();
				}
				catch(NoSuchMethodException|ClassNotFoundException|IllegalAccessException|InstantiationException|InvocationTargetException e)
				{
					return e;
				}
				final TProcessor processor=service.create();
				final String address=configuration.address;
				final TEventBusServer eventBusServer=new TEventBusServer(new TEventBusServer.Args(vertx,address).processor(processor));
				eventBusServer.serve();
				final Logger logger=container.logger();
				logger.info("EventBusServer started on address "+address);
				final THttpServer.Args httpArgs=new THttpServer.Args(vertx,configuration.port);
				httpArgs.processor(processor).protocolFactory(new TJSONProtocol.Factory());
				servers.add(new THttpServer(httpArgs));
				return null;
			});
		}
		catch(Throwable throwable)
		{
			container.logger().fatal("Failed to start verticle",throwable);
			return;
		}
		servers.forEach(TServer::serve);
	}

	@Override
	public void stop()
	{
		servers.forEach(TServer::stop);
	}
}
