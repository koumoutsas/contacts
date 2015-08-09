package com.kareebo.contacts.base;

import org.apache.thrift.TEnum;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link EnumPlaintextSerializer}
 */
public class EnumPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		validate(TestAlgorithm.A,TestAlgorithm.A.getValue());
		validate(TestAlgorithm.B,TestAlgorithm.B.getValue());
	}

	private void validate(final TestAlgorithm algorithm,final int expected)
	{
		final Vector<byte[]> bytes=new EnumPlaintextSerializer<>(algorithm).serialize();
		assertEquals(1,bytes.size());
		assertEquals(1,bytes.elementAt(0).length);
		assertEquals(expected,bytes.elementAt(0)[0]);
	}

	private enum TestAlgorithm implements TEnum
	{
		A(1),
		B(2);
		private final int value;

		TestAlgorithm(int value)
		{
			this.value=value;
		}

		public int getValue()
		{
			return value;
		}
	}
}