package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TProtocol;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link BasePlaintextSerializer}
 */
public class BasePlaintextSerializerTest
{
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testSerialize() throws Exception
	{
		final UserAgent o=new UserAgent("a","b");
		final Vector<byte[]> serialized=new BasePlaintextSerializer<>(o).serialize();
		assertEquals(BasePlaintextSerializer.LENGTH,serialized.size());
		final byte[] expected=new TSerializer().serialize(o);
		assertArrayEquals(expected,serialized.elementAt(0));
	}

	@Test
	public void testSerializeFailed() throws Exception
	{
		final UserAgent o=new UserAgent()
		{
			@Override
			public void write(TProtocol var1) throws TException
			{
				throw new TException();
			}
		};
		o.setPlatform("");
		o.setVersion("");
		thrown.expect(FailedOperation.class);
		new BasePlaintextSerializer<>(o).serialize();
	}
}