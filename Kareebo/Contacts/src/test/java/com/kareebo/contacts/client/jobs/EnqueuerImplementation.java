package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.ServiceMethod;
import org.apache.thrift.TBase;

import static org.junit.Assert.fail;

public class EnqueuerImplementation implements Enqueuer
{
	private Throwable cause;
	private ServiceMethod method;
	private TBase payload;

	public boolean job(final ServiceMethod method,final TBase payload)
	{
		return cause==null&&method.equals(this.method)&&(this.payload==null?payload==null:this.payload.equals(payload));
	}

	public boolean error(final ServiceMethod method,final Throwable cause)
	{
		return payload==null&&this.method.equals(method)&&(this.cause==null?cause==null:this.cause.equals(cause));
	}

	@Override
	public void processorError(final ServiceMethod method,final Throwable cause)
	{
		fail();
	}

	@Override
	public void protocolError(final ServiceMethod method,final Throwable cause)
	{
		this.method=method;
		this.cause=cause;
		this.payload=null;
	}

	@Override
	public void success(final String service)
	{
		this.cause=null;
		this.method=new ServiceMethod(service,null);
		this.payload=null;
	}

	@Override
	public void processor(final ServiceMethod method,final TBase payload)
	{
		this.cause=null;
		this.method=method;
		this.payload=payload;
	}

	@Override
	public void protocol(final ServiceMethod method,final TBase payload)
	{
		fail();
	}

	public boolean initialState()
	{
		return method==null&&cause==null&&payload==null;
	}
}
