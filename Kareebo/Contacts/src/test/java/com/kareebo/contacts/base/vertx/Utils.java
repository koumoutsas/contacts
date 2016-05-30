package com.kareebo.contacts.base.vertx;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.datagram.DatagramSocket;
import org.vertx.java.core.datagram.InternetProtocolFamily;
import org.vertx.java.core.dns.DnsClient;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.http.*;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LogDelegate;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.shareddata.SharedData;
import org.vertx.java.core.sockjs.SockJSServer;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;

/**
 * Mocked implementations of various Vertx classes for testing
 */
public class Utils
{
	public static class Container implements org.vertx.java.platform.Container
	{
		public static Object lastFatal;
		public static Throwable lastFatalThrowable;
		private final JsonObject configuration;

		public Container(final String configuration)
		{
			this.configuration=new JsonObject(configuration);
		}

		@Override
		public void deployWorkerVerticle(final String main)
		{
		}

		@Override
		public void deployWorkerVerticle(final String main,final int instances)
		{
		}

		@Override
		public void deployWorkerVerticle(final String main,final JsonObject config)
		{
		}

		@Override
		public void deployWorkerVerticle(final String main,final JsonObject config,final int instances)
		{
		}

		@Override
		public void deployWorkerVerticle(final String main,final JsonObject config,final int instances,final boolean multiThreaded)
		{
		}

		@Override
		public void deployWorkerVerticle(final String main,final JsonObject config,final int instances,final boolean multiThreaded,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void deployModule(final String moduleName)
		{
		}

		@Override
		public void deployModule(final String moduleName,final int instances)
		{
		}

		@Override
		public void deployModule(final String moduleName,final JsonObject config)
		{
		}

		@Override
		public void deployModule(final String moduleName,final JsonObject config,final int instances)
		{
		}

		@Override
		public void deployModule(final String moduleName,final JsonObject config,final int instances,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void deployModule(final String moduleName,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void deployModule(final String moduleName,final JsonObject config,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void deployModule(final String moduleName,final int instances,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void deployVerticle(final String main)
		{
		}

		@Override
		public void deployVerticle(final String main,final int instances)
		{
		}

		@Override
		public void deployVerticle(final String main,final JsonObject config)
		{
		}

		@Override
		public void deployVerticle(final String main,final JsonObject config,final int instances)
		{
		}

		@Override
		public void deployVerticle(final String main,final JsonObject config,final int instances,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void deployVerticle(final String main,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void deployVerticle(final String main,final JsonObject config,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void deployVerticle(final String main,final int instances,final Handler<AsyncResult<String>> doneHandler)
		{
		}

		@Override
		public void undeployVerticle(final String deploymentID)
		{
		}

		@Override
		public void undeployVerticle(final String deploymentID,final Handler<AsyncResult<Void>> doneHandler)
		{
		}

		@Override
		public void undeployModule(final String deploymentID)
		{
		}

		@Override
		public void undeployModule(final String deploymentID,final Handler<AsyncResult<Void>> doneHandler)
		{
		}

		@Override
		public JsonObject config()
		{
			return configuration;
		}

		@Override
		public org.vertx.java.core.logging.Logger logger()
		{
			return new Logger(new LogDelegate()
			{
				@Override
				public boolean isInfoEnabled()
				{
					return false;
				}

				@Override
				public boolean isDebugEnabled()
				{
					return false;
				}

				@Override
				public boolean isTraceEnabled()
				{
					return false;
				}

				@Override
				public void fatal(final Object message)
				{
					Container.lastFatal=message;
					Container.lastFatalThrowable=null;
				}

				@Override
				public void fatal(final Object message,final Throwable t)
				{
					Container.lastFatal=message;
					Container.lastFatalThrowable=t;
				}

				@Override
				public void error(final Object message)
				{
				}

				@Override
				public void error(final Object message,final Throwable t)
				{
				}

				@Override
				public void warn(final Object message)
				{
				}

				@Override
				public void warn(final Object message,final Throwable t)
				{
				}

				@Override
				public void info(final Object message)
				{
				}

				@Override
				public void info(final Object message,final Throwable t)
				{
				}

				@Override
				public void debug(final Object message)
				{
				}

				@Override
				public void debug(final Object message,final Throwable t)
				{
				}

				@Override
				public void trace(final Object message)
				{
				}

				@Override
				public void trace(final Object message,final Throwable t)
				{
				}
			});
		}

		@Override
		public void exit()
		{
		}

		@Override
		public Map<String,String> env()
		{
			return null;
		}
	}

	public static class Vertx implements org.vertx.java.core.Vertx
	{
		public boolean handlerRegistered;
		private final EventBus eventBus=new EventBus()
		{
			@Override
			public void close(final Handler<AsyncResult<Void>> doneHandler)
			{
			}

			@Override
			public EventBus send(final String address,final Object message)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Object message,final Handler<Message> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Object message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final JsonObject message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final JsonObject message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final JsonObject message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final JsonArray message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final JsonArray message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final JsonArray message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Buffer message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Buffer message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Buffer message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final byte[] message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final byte[] message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final byte[] message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final String message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final String message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final String message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Integer message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Integer message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Integer message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Long message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Long message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Long message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Float message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Float message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Float message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Double message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Double message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Double message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Boolean message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Boolean message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Boolean message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Short message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Short message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Short message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Character message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Character message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Character message)
			{
				return null;
			}

			@Override
			public <T> EventBus send(final String address,final Byte message,final Handler<Message<T>> replyHandler)
			{
				return null;
			}

			@Override
			public <T> EventBus sendWithTimeout(final String address,final Byte message,final long timeout,final Handler<AsyncResult<Message<T>>> replyHandler)
			{
				return null;
			}

			@Override
			public EventBus send(final String address,final Byte message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Object message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final JsonObject message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final JsonArray message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Buffer message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final byte[] message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final String message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Integer message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Long message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Float message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Double message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Boolean message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Short message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Character message)
			{
				return null;
			}

			@Override
			public EventBus publish(final String address,final Byte message)
			{
				return null;
			}

			@Override
			public EventBus unregisterHandler(final String address,final Handler<? extends Message> handler,final Handler<AsyncResult<Void>> resultHandler)
			{
				handlerRegistered=false;
				return this;
			}

			@Override
			public EventBus unregisterHandler(final String address,final Handler<? extends Message> handler)
			{
				handlerRegistered=false;
				return this;
			}

			@Override
			public EventBus registerHandler(final String address,final Handler<? extends Message> handler,final Handler<AsyncResult<Void>> resultHandler)
			{
				handlerRegistered=true;
				return this;
			}

			@Override
			public EventBus registerHandler(final String address,final Handler<? extends Message> handler)
			{
				handlerRegistered=true;
				return this;
			}

			@Override
			public EventBus registerLocalHandler(final String address,final Handler<? extends Message> handler)
			{
				return null;
			}

			@Override
			public EventBus setDefaultReplyTimeout(final long timeoutMs)
			{
				return null;
			}

			@Override
			public long getDefaultReplyTimeout()
			{
				return 0;
			}
		};
		public boolean serverWorking;

		@Override
		public NetServer createNetServer()
		{
			return null;
		}

		@Override
		public NetClient createNetClient()
		{
			return null;
		}

		@Override
		public HttpServer createHttpServer()
		{
			return new HttpServer()
			{
				@Override
				public HttpServer requestHandler(final Handler<HttpServerRequest> requestHandler)
				{
					return null;
				}

				@Override
				public Handler<HttpServerRequest> requestHandler()
				{
					return null;
				}

				@Override
				public HttpServer websocketHandler(final Handler<ServerWebSocket> wsHandler)
				{
					return null;
				}

				@Override
				public Handler<ServerWebSocket> websocketHandler()
				{
					return null;
				}

				@Override
				public HttpServer listen(final int port)
				{
					serverWorking=true;
					return this;
				}

				@Override
				public HttpServer listen(final int port,final Handler<AsyncResult<HttpServer>> listenHandler)
				{
					serverWorking=true;
					return this;
				}

				@Override
				public HttpServer listen(final int port,final String host)
				{
					serverWorking=true;
					return this;
				}

				@Override
				public HttpServer listen(final int port,final String host,final Handler<AsyncResult<HttpServer>> listenHandler)
				{
					serverWorking=true;
					return this;
				}

				@Override
				public void close()
				{
					serverWorking=false;
				}

				@Override
				public void close(final Handler<AsyncResult<Void>> doneHandler)
				{
					serverWorking=false;
				}

				@Override
				public HttpServer setCompressionSupported(final boolean compressionSupported)
				{
					return null;
				}

				@Override
				public boolean isCompressionSupported()
				{
					return false;
				}

				@Override
				public HttpServer setMaxWebSocketFrameSize(final int maxSize)
				{
					return null;
				}

				@Override
				public int getMaxWebSocketFrameSize()
				{
					return 0;
				}

				@Override
				public HttpServer setWebSocketSubProtocols(final String... subProtocols)
				{
					return null;
				}

				@Override
				public Set<String> getWebSocketSubProtocols()
				{
					return null;
				}

				@Override
				public HttpServer setAcceptBacklog(final int backlog)
				{
					return null;
				}

				@Override
				public int getAcceptBacklog()
				{
					return 0;
				}

				@Override
				public HttpServer setSendBufferSize(final int size)
				{
					return null;
				}

				@Override
				public HttpServer setClientAuthRequired(final boolean required)
				{
					return null;
				}

				@Override
				public boolean isClientAuthRequired()
				{
					return false;
				}

				@Override
				public HttpServer setSSL(final boolean ssl)
				{
					return null;
				}

				@Override
				public HttpServer setTCPNoDelay(final boolean tcpNoDelay)
				{
					return null;
				}

				@Override
				public HttpServer setReceiveBufferSize(final int size)
				{
					return null;
				}

				@Override
				public HttpServer setReuseAddress(final boolean reuse)
				{
					return null;
				}

				@Override
				public HttpServer setTrafficClass(final int trafficClass)
				{
					return null;
				}

				@Override
				public int getSendBufferSize()
				{
					return 0;
				}

				@Override
				public int getReceiveBufferSize()
				{
					return 0;
				}

				@Override
				public boolean isReuseAddress()
				{
					return false;
				}

				@Override
				public int getTrafficClass()
				{
					return 0;
				}

				@Override
				public boolean isSSL()
				{
					return false;
				}

				@Override
				public HttpServer setSSLContext(final SSLContext sslContext)
				{
					return null;
				}

				@Override
				public HttpServer setKeyStorePath(final String path)
				{
					return null;
				}

				@Override
				public String getKeyStorePath()
				{
					return null;
				}

				@Override
				public HttpServer setKeyStorePassword(final String pwd)
				{
					return null;
				}

				@Override
				public String getKeyStorePassword()
				{
					return null;
				}

				@Override
				public HttpServer setTrustStorePath(final String path)
				{
					return null;
				}

				@Override
				public String getTrustStorePath()
				{
					return null;
				}

				@Override
				public HttpServer setTrustStorePassword(final String pwd)
				{
					return null;
				}

				@Override
				public String getTrustStorePassword()
				{
					return null;
				}

				@Override
				public HttpServer setTCPKeepAlive(final boolean keepAlive)
				{
					return null;
				}

				@Override
				public HttpServer setSoLinger(final int linger)
				{
					return null;
				}

				@Override
				public HttpServer setUsePooledBuffers(final boolean pooledBuffers)
				{
					return null;
				}

				@Override
				public boolean isTCPNoDelay()
				{
					return false;
				}

				@Override
				public boolean isTCPKeepAlive()
				{
					return false;
				}

				@Override
				public int getSoLinger()
				{
					return 0;
				}

				@Override
				public boolean isUsePooledBuffers()
				{
					return false;
				}
			};
		}

		@Override
		public HttpClient createHttpClient()
		{
			return new HttpClient()
			{
				@Override
				public HttpClient exceptionHandler(final Handler<Throwable> handler)
				{
					return null;
				}

				@Override
				public HttpClient setMaxPoolSize(final int maxConnections)
				{
					return null;
				}

				@Override
				public int getMaxPoolSize()
				{
					return 0;
				}

				@Override
				public HttpClient setMaxWaiterQueueSize(final int maxWaiterQueueSize)
				{
					return null;
				}

				@Override
				public int getMaxWaiterQueueSize()
				{
					return 0;
				}

				@Override
				public HttpClient setConnectionMaxOutstandingRequestCount(final int connectionMaxOutstandingRequestCount)
				{
					return null;
				}

				@Override
				public int getConnectionMaxOutstandingRequestCount()
				{
					return 0;
				}

				@Override
				public HttpClient setKeepAlive(final boolean keepAlive)
				{
					return null;
				}

				@Override
				public boolean isKeepAlive()
				{
					return false;
				}

				@Override
				public HttpClient setPipelining(final boolean pipelining)
				{
					return null;
				}

				@Override
				public boolean isPipelining()
				{
					return false;
				}

				@Override
				public HttpClient setPort(final int port)
				{
					return this;
				}

				@Override
				public int getPort()
				{
					return 0;
				}

				@Override
				public HttpClient setHost(final String host)
				{
					return null;
				}

				@Override
				public String getHost()
				{
					return null;
				}

				@Override
				public HttpClient connectWebsocket(final String uri,final Handler<WebSocket> wsConnect)
				{
					return null;
				}

				@Override
				public HttpClient connectWebsocket(final String uri,final WebSocketVersion wsVersion,final Handler<WebSocket> wsConnect)
				{
					return null;
				}

				@Override
				public HttpClient connectWebsocket(final String uri,final WebSocketVersion wsVersion,final MultiMap headers,final Handler<WebSocket> wsConnect)
				{
					return null;
				}

				@Override
				public HttpClient connectWebsocket(final String uri,final WebSocketVersion wsVersion,final MultiMap headers,final Set<String> subprotocols,final Handler<WebSocket> wsConnect)
				{
					return null;
				}

				@Override
				public HttpClient getNow(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClient getNow(final String uri,final MultiMap headers,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest options(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest get(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest head(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest post(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest put(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest delete(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest trace(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest connect(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest patch(final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public HttpClientRequest request(final String method,final String uri,final Handler<HttpClientResponse> responseHandler)
				{
					return null;
				}

				@Override
				public void close()
				{
				}

				@Override
				public HttpClient setVerifyHost(final boolean verifyHost)
				{
					return null;
				}

				@Override
				public boolean isVerifyHost()
				{
					return false;
				}

				@Override
				public HttpClient setConnectTimeout(final int timeout)
				{
					return null;
				}

				@Override
				public int getConnectTimeout()
				{
					return 0;
				}

				@Override
				public HttpClient setTryUseCompression(final boolean tryUseCompression)
				{
					return null;
				}

				@Override
				public boolean getTryUseCompression()
				{
					return false;
				}

				@Override
				public HttpClient setMaxWebSocketFrameSize(final int maxSize)
				{
					return null;
				}

				@Override
				public int getMaxWebSocketFrameSize()
				{
					return 0;
				}

				@Override
				public HttpClient setTrustAll(final boolean trustAll)
				{
					return null;
				}

				@Override
				public boolean isTrustAll()
				{
					return false;
				}

				@Override
				public HttpClient setSendBufferSize(final int size)
				{
					return null;
				}

				@Override
				public HttpClient setReceiveBufferSize(final int size)
				{
					return null;
				}

				@Override
				public HttpClient setReuseAddress(final boolean reuse)
				{
					return null;
				}

				@Override
				public HttpClient setTrafficClass(final int trafficClass)
				{
					return null;
				}

				@Override
				public int getSendBufferSize()
				{
					return 0;
				}

				@Override
				public int getReceiveBufferSize()
				{
					return 0;
				}

				@Override
				public boolean isReuseAddress()
				{
					return false;
				}

				@Override
				public int getTrafficClass()
				{
					return 0;
				}

				@Override
				public HttpClient setSSL(final boolean ssl)
				{
					return null;
				}

				@Override
				public boolean isSSL()
				{
					return false;
				}

				@Override
				public HttpClient setSSLContext(final SSLContext sslContext)
				{
					return null;
				}

				@Override
				public HttpClient setKeyStorePath(final String path)
				{
					return null;
				}

				@Override
				public String getKeyStorePath()
				{
					return null;
				}

				@Override
				public HttpClient setKeyStorePassword(final String pwd)
				{
					return null;
				}

				@Override
				public String getKeyStorePassword()
				{
					return null;
				}

				@Override
				public HttpClient setTrustStorePath(final String path)
				{
					return null;
				}

				@Override
				public String getTrustStorePath()
				{
					return null;
				}

				@Override
				public HttpClient setTrustStorePassword(final String pwd)
				{
					return null;
				}

				@Override
				public String getTrustStorePassword()
				{
					return null;
				}

				@Override
				public HttpClient setTCPNoDelay(final boolean tcpNoDelay)
				{
					return null;
				}

				@Override
				public HttpClient setTCPKeepAlive(final boolean keepAlive)
				{
					return null;
				}

				@Override
				public HttpClient setSoLinger(final int linger)
				{
					return null;
				}

				@Override
				public HttpClient setUsePooledBuffers(final boolean pooledBuffers)
				{
					return null;
				}

				@Override
				public boolean isTCPNoDelay()
				{
					return false;
				}

				@Override
				public boolean isTCPKeepAlive()
				{
					return false;
				}

				@Override
				public int getSoLinger()
				{
					return 0;
				}

				@Override
				public boolean isUsePooledBuffers()
				{
					return false;
				}
			};
		}

		@Override
		public DatagramSocket createDatagramSocket(final InternetProtocolFamily family)
		{
			return null;
		}

		@Override
		public SockJSServer createSockJSServer(final HttpServer httpServer)
		{
			return null;
		}

		@Override
		public FileSystem fileSystem()
		{
			return null;
		}

		@Override
		public EventBus eventBus()
		{
			return eventBus;
		}

		@Override
		public DnsClient createDnsClient(final InetSocketAddress... dnsServers)
		{
			return null;
		}

		@Override
		public SharedData sharedData()
		{
			return null;
		}

		@Override
		public long setTimer(final long delay,final Handler<Long> handler)
		{
			return 0;
		}

		@Override
		public long setPeriodic(final long delay,final Handler<Long> handler)
		{
			return 0;
		}

		@Override
		public boolean cancelTimer(final long id)
		{
			return false;
		}

		@Override
		public Context currentContext()
		{
			return null;
		}

		@Override
		public void runOnContext(final Handler<Void> action)
		{
		}

		@Override
		public boolean isEventLoop()
		{
			return false;
		}

		@Override
		public boolean isWorker()
		{
			return true;
		}

		@Override
		public void stop()
		{
		}
	}
}
