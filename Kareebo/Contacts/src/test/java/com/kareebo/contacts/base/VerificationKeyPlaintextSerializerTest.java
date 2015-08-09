package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.VerificationKey;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link VerificationKeyPlaintextSerializer}
 */
public class VerificationKeyPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final SignatureAlgorithm algorithm=SignatureAlgorithm.SHA256withECDSAprime239v1;
		final byte[] bytes="abc".getBytes();
		final ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
		byteBuffer.mark();
		final Vector<byte[]> plaintext=new VerificationKeyPlaintextSerializer(new VerificationKey(byteBuffer,algorithm)).serialize();
		assertEquals(EnumPlaintextSerializer.LENGTH+1,plaintext.size());
		assertArrayEquals(new EnumPlaintextSerializer<>(algorithm).serialize().elementAt(0),plaintext
			                                                                                    .elementAt(0));
		assertArrayEquals(bytes,plaintext.elementAt(1));
	}
}