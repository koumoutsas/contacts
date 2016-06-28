package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.crypto.TestSignatureKeyPair;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Signer}
 */
public class SignerTest
{
	@Test
	public void test() throws Exception
	{
		final ClientId clientId=new ClientId(0,0);
		final SignatureAlgorithm algorithm=SignatureAlgorithm.SHA512withECDSAprime239v1;
		final LongId id=new LongId(5);
		final TestSignatureKeyPair testSignatureKeyPair=new TestSignatureKeyPair();
		final SignatureBuffer result=new Signer(testSignatureKeyPair.signingKey(),clientId).sign(id);
		assertEquals(algorithm,result.getAlgorithm());
		assertEquals(clientId,result.getClient());
		assertTrue(new Verifier(testSignatureKeyPair.getPublic(),algorithm).verify(id,result.getBuffer()));
	}
}