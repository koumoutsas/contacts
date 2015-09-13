package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link TBase}
 */
public class BasePlaintextSerializer<T extends TBase> implements PlaintextSerializer
{
	/**
	 * The length of the returned vector
	 */
	public final static int LENGTH=1;
	private static final Logger logger=LoggerFactory.getLogger(BasePlaintextSerializer.class.getName());
	final private T object;

	BasePlaintextSerializer(final T object)
	{
		this.object=object;
	}

	@Override
	public Vector<byte[]> serialize() throws FailedOperation
	{
		final Vector<byte[]> ret=new Vector<>(LENGTH);
		try
		{
			ret.add(new TSerializer().serialize(object));
		}
		catch(TException e)
		{
			logger.error("Serialization error",e);
			throw new FailedOperation();
		}
		return ret;
	}
}
