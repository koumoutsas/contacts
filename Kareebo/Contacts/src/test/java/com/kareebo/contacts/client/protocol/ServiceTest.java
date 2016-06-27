package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.*;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.crypto.TestKeyPair;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureAlgorithm;
import com.kareebo.contacts.thrift.SignatureBuffer;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.async.TAsyncMethodCall;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Nonnull;
import java.security.*;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Unit test for {@link Service}
 */
public class ServiceTest
{
	@Rule
	final public ExpectedException thrown=ExpectedException.none();
	final private ClientId clientId=new ClientId(0,0);
	final private SignatureAlgorithm algorithm=SignatureAlgorithm.SHA512withECDSAprime239v1;
	final private LongId id=new LongId(5);
	final private TestKeyPair testKeyPair;

	public ServiceTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
	{
		testKeyPair=new TestKeyPair();
	}

	@Test
	public void testSign() throws Exception
	{
		final SignatureBuffer result=new ServiceImplementation(testKeyPair.signingKey(),clientId).getSignature(id);
		assertEquals(algorithm,result.getAlgorithm());
		assertEquals(clientId,result.getClient());
		assertTrue(new Verifier(testKeyPair.getPublic(),algorithm).verify(id,result.getBuffer()));
	}

	@Test
	public void testIllegalJobType() throws Exception
	{
		thrown.expect(IllegalArgumentException.class);
		new ServiceImplementation(testKeyPair.signingKey(),clientId).run(null,new Enqueuers(new HashMap<>(),new FinalResultEnqueuer()
		{
			@Override
			public void error(@Nonnull final ErrorJob job)
			{
			}

			@Override
			public void success(@Nonnull final SuccessJob job)
			{
			}
		}));
	}

	@Test
	public void test() throws Exception
	{
		final LongId expected=new LongId(0);
		final ServiceImplementation serviceImplementation=new ServiceImplementation(testKeyPair.signingKey(),
			                                                                           clientId);
		serviceImplementation.run(expected,new Enqueuers(JobType
			                                                 .Processor,(jobs)->{
		},new FinalResultEnqueuer()
		{
			@Override
			public void error(@Nonnull final ErrorJob job)
			{
			}

			@Override
			public void success(@Nonnull final SuccessJob job)
			{
			}
		}));
		assertSame(expected,serviceImplementation.lastPayload);
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
		TBase lastPayload;

		ServiceImplementation(final SigningKey signingKey,final ClientId clientId)
		{
			super(new Context(),new MockClientManager<Void>(true)
				{
					@Override
					protected void callInternal(final TAsyncMethodCall method)
					{
					}
				},
				signingKey,clientId);
		}

		@Override
		protected MyAsyncClient construct(@Nonnull final TAsyncClientManager asyncClientManager)
		{
			return null;
		}

		@Nonnull
		@Override
		protected ServiceMethod[] methodNames()
		{
			return new ServiceMethod[]{new ServiceMethod(this.getClass().getSimpleName(),"1")};
		}

		@Nonnull
		@Override
		protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
		{
			return new com.kareebo.contacts.client.jobs.Service.Functor[]{new Functor<TBase>()
			{
				@Override
				protected void runInternal(@Nonnull final MyAsyncClient asyncClient,@Nonnull final TBase payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
				}
			}};
		}

		SignatureBuffer getSignature(final TBase object) throws NoSuchProviderException, TException, NoSuchAlgorithmException,
			                                                        InvalidKeyException,
			                                                        SignatureException, InvalidAlgorithmParameterException
		{
			return sign(object);
		}

		void run(final TBase payload,final Enqueuers enqueuers) throws Exception
		{
			new Functor<TBase>()
			{
				@Override
				protected void runInternal(@Nonnull final MyAsyncClient asyncClient,@Nonnull final TBase payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,@Nonnull final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					lastPayload=payload;
				}
			}.run(payload,enqueuers);
		}
	}
}