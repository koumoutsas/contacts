package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TDeserializer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * Unit test {@link NotificationObject}
 */
public class NotificationObjectTest
{
	@Test
	public void testGetMethod() throws Exception
	{
		final ServiceMethod method=new ServiceMethod("1","2");
		assertEquals(method,new NotificationObject(method,new LongId()).getMethod());
	}

	@Test
	public void testGetPayload() throws Exception
	{
		final LongId expected=new LongId(9);
		final ByteBuffer payload=new NotificationObject(new ServiceMethod(),expected).getPayload();
		payload.rewind();
		final byte[] bytes=new byte[payload.remaining()];
		payload.get(bytes);
		final LongId id=new LongId();
		new TDeserializer().deserialize(id,bytes);
		assertEquals(expected,id);
	}
}