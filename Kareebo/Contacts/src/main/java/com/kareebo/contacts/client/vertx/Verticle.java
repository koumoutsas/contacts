package com.kareebo.contacts.client.vertx;

import com.kareebo.contacts.client.protocol.ServiceDispatcher;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.transport.THttpClientTransport;
import org.apache.thrift.transport.TTransportException;
import org.vertx.java.core.VoidHandler;

/**
 * Client verticle
 */
class Verticle extends org.vertx.java.platform.Verticle
{
	private ServiceDispatcher serviceDispatcher;

	@Override
	public void start()
	{
		serviceDispatcher=new ServiceDispatcher();
		testHttpClient(new VoidHandler()
		{
			@Override
			protected void handle()
			{
				container.logger().info("testHttpClient > Complete.");
			}
		});
	}

	private void testHttpClient(final VoidHandler completeHandler)
	{
		container.logger().info("testHttpClient > Start.");
		int port=container.config().getInteger("http_port");
		THttpClientTransport transport=new THttpClientTransport(
			                                                       new THttpClientTransport.Args(vertx,port));
		try
		{
			transport.open();
		}
		catch(TTransportException e)
		{
			e.printStackTrace();
			completeHandler.handle(null);
			return;
		}
		TAsyncClientManager clientManager=new TAsyncClientManager(transport,new TJSONProtocol.Factory());
		ModifyUserAgent client=new ModifyUserAgent(clientManager);
		perform(client,completeHandler);
	}

	private void perform(final Calculator.VertxClient client,VoidHandler completeHandler)
	{
		final TestCompleteCounter counter=new TestCompleteCounter(completeHandler);
		client.ping(new AsyncResultHandler<Void>()
		{
			@Override
			public void handle(AsyncResult<Void> event)
			{
				System.out.println("C < ping()");
				counter.decrease();
			}
		});
		counter.increase();
		client.add(1,1,new AsyncResultHandler<Integer>()
		{
			@Override
			public void handle(AsyncResult<Integer> event)
			{
				int sum=event.result();
				System.out.println("C < 1+1="+sum);
				counter.decrease();
			}
		});
		counter.increase();
		Work work=new Work();
		work.op=Operation.DIVIDE;
		work.num1=1;
		work.num2=0;
		client.calculate(1,work,new AsyncResultHandler<Integer>()
		{
			@Override
			public void handle(AsyncResult<Integer> event)
			{
				InvalidOperation io=(InvalidOperation)event.cause();
				System.out.println("C < Invalid operation: "+io.why);
				counter.decrease();
			}
		});
		counter.increase();
		work.op=Operation.SUBTRACT;
		work.num1=15;
		work.num2=10;
		client.calculate(1,work,new AsyncResultHandler<Integer>()
		{
			@Override
			public void handle(AsyncResult<Integer> event)
			{
				int diff=event.result();
				System.out.println("C < 15-10="+diff);
				client.getStruct(1,new AsyncResultHandler<SharedStruct>()
				{
					@Override
					public void handle(AsyncResult<SharedStruct> event)
					{
						SharedStruct log=event.result();
						System.out.println("C < Check log: "+log.value);
						counter.decrease();
					}
				});
			}
		});
		counter.increase();
	}

	private static class TestCompleteCounter
	{
		final VoidHandler completeHandler;
		int counter=0;

		TestCompleteCounter(VoidHandler handler)
		{
			completeHandler=handler;
		}

		void increase()
		{
			++counter;
		}

		void decrease()
		{
			if(counter==0)
			{
				throw new IllegalStateException("counter == 0");
			}
			if(--counter==0)
			{
				completeHandler.handle(null);
			}
		}
	}
}
