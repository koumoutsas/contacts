package com.kareebo.contacts.base;

import com.kareebo.contacts.common.Algorithm;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link AlgorithmPlaintextSerializer}
 */
public class AlgorithmPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		validate(Algorithm.SHA256,0);
		validate(Algorithm.SHA256withECDSAprime239v1,1);
		validate(Algorithm.Fake,2);
	}

	private void validate(final Algorithm algorithm,final int expected)
	{
		final Vector<byte[]> bytes=new AlgorithmPlaintextSerializer(algorithm).serialize();
		assertEquals(1,bytes.size());
		assertEquals(1,bytes.elementAt(0).length);
		assertEquals(expected,bytes.elementAt(0)[0]);
	}
}