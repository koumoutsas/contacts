package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuer;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.junit.Test;

import java.security.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Service}
 */
public class ServiceTest
{
	@Test
	public void testSign() throws Exception
	{
		final ClientId clientId=new ClientId(0,0);
		final SignatureAlgorithm algorithm=SignatureAlgorithm.SHA256withECDSAprime239v1;
		final LongId id=new LongId(5);
		final KeyPair keyPair=new KeyPair();
		final SignatureBuffer result=new ServiceImplementation(new SigningKey(keyPair.getPrivate(),algorithm),clientId).getSignature(id);
		assertEquals(algorithm,result.getAlgorithm());
		assertEquals(clientId,result.getClient());
		assertTrue(new Verifier(keyPair.getPublic(),algorithm).verify(id,result.getBuffer()));
	}

	private static class MyAsyncClient extends TAsyncClient
	{
		public MyAsyncClient(final TAsyncClientManager clientManager)
		{
			super(clientManager);
		}
	}

	private static class ServiceImplementation extends Service<MyAsyncClient>
	{
		ServiceImplementation(final SigningKey signingKey,final ClientId clientId)
		{
			super(null,signingKey,clientId);
		}

		@Override
		protected MyAsyncClient construct(final TAsyncClientManager asyncClientManager)
		{
			return null;
		}

		@Override
		protected void runInternal(final ServiceMethod method,final TBase payload,final Enqueuer enqueuer) throws Exception
		{
		}

		SignatureBuffer getSignature(final TBase object) throws NoSuchProviderException, TException, NoSuchAlgorithmException,
			                                                        InvalidKeyException,
			                                                        SignatureException, InvalidAlgorithmParameterException
		{
			return sign(object);
		}
	}
}