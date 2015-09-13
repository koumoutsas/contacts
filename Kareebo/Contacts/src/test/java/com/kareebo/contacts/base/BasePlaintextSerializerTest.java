package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TProtocol;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertArrayEquals;

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
		final byte[] serialized=new BasePlaintextSerializer<>(o).serialize();
		final byte[] expected=new TSerializer().serialize(o);
		assertArrayEquals(expected,serialized);
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