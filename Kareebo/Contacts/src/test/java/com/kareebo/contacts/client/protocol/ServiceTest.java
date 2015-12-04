package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.SignatureBuffer;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.security.*;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Service}
 */
public class ServiceTest
{
	final private ClientId clientId=new ClientId(0,0);
	final private SignatureAlgorithm algorithm=SignatureAlgorithm.SHA256withECDSAprime239v1;
	final private LongId id=new LongId(5);
	final private KeyPair keyPair;
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	public ServiceTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
		keyPair=new KeyPair();
	}

	@Test
	public void testSign() throws Exception
	{
		final SignatureBuffer result=new ServiceImplementation(new SigningKey(keyPair.getPrivate(),algorithm),clientId).getSignature(id);
		assertEquals(algorithm,result.getAlgorithm());
		assertEquals(clientId,result.getClient());
		assertTrue(new Verifier(keyPair.getPublic(),algorithm).verify(id,result.getBuffer()));
	}

	@Test
	public void testIllegalJobType() throws Exception
	{
		thrown.expect(IllegalArgumentException.class);
		new ServiceImplementation(new SigningKey(keyPair.getPrivate(),algorithm),clientId).run(null,null,new Enqueuers(new HashMap<JobType,
				                                                                                                                          IntermediateResultEnqueuer>(),null));
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
		protected void runInternal(final ServiceMethod method,final TBase payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
		{
		}

		void run(final ServiceMethod method,final TBase payload,final Enqueuers enqueuers) throws Exception
		{
			runInternal(method,payload,enqueuers);
		}

		SignatureBuffer getSignature(final TBase object) throws NoSuchProviderException, TException, NoSuchAlgorithmException,
			                                                        InvalidKeyException,
			                                                        SignatureException, InvalidAlgorithmParameterException
		{
			return sign(object);
		}
	}
}