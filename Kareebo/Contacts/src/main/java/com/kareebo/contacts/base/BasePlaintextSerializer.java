package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link PlaintextSerializer} for {@link TBase}
 */
public class BasePlaintextSerializer<T extends TBase> implements PlaintextSerializer
{
	private static final Logger logger=LoggerFactory.getLogger(BasePlaintextSerializer.class.getName());
	final private T object;

	public BasePlaintextSerializer(final T object)
	{
		this.object=object;
	}

	@Override
	public byte[] serialize() throws FailedOperation
	{
		try
		{
			return new TSerializer().serialize(object);
		}
		catch(TException e)
		{
			logger.error("Serialization error",e);
			throw new FailedOperation();
		}
	}
}
