package com.kareebo.contacts.client.service;

import com.kareebo.contacts.client.SigningKey;
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
		final SignatureAlgorithm algorithm=SignatureAlgorithm.SHA256withECDSAprime239v1;
		final LongId id=new LongId(5);
		final KeyPair keyPair=new KeyPair();
		final SignatureBuffer result=new Signer(new SigningKey(keyPair.getPrivate(),algorithm),clientId).sign(id);
		assertEquals(algorithm,result.getAlgorithm());
		assertEquals(clientId,result.getClient());
		assertTrue(new Verifier(keyPair.getPublic(),algorithm).verify(id,result.getBuffer()));
	}
}