package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

/**
 * Serializer encapsulation for tests
 */
public class PlaintextSerializer<T extends TBase>
{
	final private T object;

	public PlaintextSerializer(final T object)
	{
		this.object=object;
	}

	public byte[] serialize() throws FailedOperation
	{
		try
		{
			return new TSerializer().serialize(object);
		}
		catch(TException e)
		{
			throw new FailedOperation();
		}
	}
}
