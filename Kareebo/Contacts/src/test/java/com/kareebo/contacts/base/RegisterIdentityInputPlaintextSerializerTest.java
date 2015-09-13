package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link RegisterIdentityInputPlaintextSerializer}
 */
public class RegisterIdentityInputPlaintextSerializerTest
{
	@Test
	public void testSerialize() throws Exception
	{
		final ByteBuffer b=ByteBuffer.wrap("1".getBytes());
		final EncryptionKey encryption=new EncryptionKey(b,EncryptionAlgorithm.Fake);
		final VerificationKey verification=new VerificationKey(b,SignatureAlgorithm.Fake);
		final PublicKeys publicKeys=new PublicKeys(encryption,verification);
		final HashBuffer uA=new HashBuffer(b,HashAlgorithm.Fake);
		final Long userIdA=(long)10;
		final Set<HashBuffer> uSet=new HashSet<>(2);
		uSet.add(uA);
		final HashBuffer uB=new HashBuffer(b,HashAlgorithm.SHA256);
		uSet.add(uB);
		final UserAgent userAgent=new UserAgent("a","b");
		final long deviceToken=67;
		final Vector<byte[]> plaintext=new RegisterIdentityInputPlaintextSerializer(new RegisterIdentityInput(publicKeys,uA,userIdA,uSet,
			                                                                                                     uB,userAgent,deviceToken))
			                               .serialize();
		assertEquals(PublicKeysPlaintextSerializer.LENGTH+(uSet.size()+2)*HashBufferPlaintextSerializer
			                                                                  .LENGTH+LongPlaintextSerializer
				                                                                          .LENGTH+UserAgentPlaintextSerializer
					                                                                                  .LENGTH+LongPlaintextSerializer.LENGTH,plaintext.size());
		final Vector<byte[]> expected=new Vector<>(plaintext.size());
		expected.addAll(new PublicKeysPlaintextSerializer(publicKeys).serialize());
		expected.addAll(new HashBufferPlaintextSerializer(uA).serialize());
		expected.addAll(new LongPlaintextSerializer(userIdA).serialize());
		expected.addAll(new CollectionPlaintextSerializer<>(uSet).serialize());
		expected.addAll(new HashBufferPlaintextSerializer(uB).serialize());
		expected.addAll(new UserAgentPlaintextSerializer(userAgent).serialize());
		expected.addAll(new LongPlaintextSerializer(deviceToken).serialize());
		for(int i=0;i<expected.size();++i)
		{
			assertArrayEquals(expected.elementAt(i),plaintext.elementAt(i));
		}
	}
}