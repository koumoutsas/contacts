package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.ContactOperationType;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ContactOperationTypePlaintextSerializer}
 */
public class ContactOperationTypePlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		validate(ContactOperationType.Add,0);
		validate(ContactOperationType.Delete,1);
	}

	private void validate(final ContactOperationType contactOperationType,final int expected)
	{
		final Vector<byte[]> addBytes=new ContactOperationTypePlaintextSerializer(contactOperationType)
			                              .serialize();
		assertEquals(1,addBytes.size());
		assertEquals(1,addBytes.elementAt(0).length);
		assertEquals(expected,addBytes.elementAt(0)[0]);
	}
}