package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.client.persistentStorage.PersistedObjectRetriever;
import com.kareebo.contacts.client.persistentStorage.PersistentStorageImplementation;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;
import org.apache.thrift.TBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link Service}
 */
public class ServiceTest
{
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testIllegalJobType() throws Exception
	{
		thrown.expect(IllegalArgumentException.class);
		new ServiceImplementation(new PersistedObjectRetriever(new PersistentStorageImplementation())).run(null,new Enqueuers(new HashMap<>()
			                                                                                                                     ,null));
	}

	@Test
	public void test() throws Exception
	{
		final LongId expected=new LongId(8);
		final PersistedObjectRetriever persistedObjectRetriever=new PersistedObjectRetriever(new PersistentStorageImplementation());
		new ServiceImplementation(persistedObjectRetriever).run(expected,new Enqueuers(JobType
			                                                                               .Protocol,(type,method,context,payload)->{
		},new FinalResultEnqueuer()
		{
			@Override
			public void success(final JobType type,final String service,final SuccessCode result)
			{
			}

			@Override
			public void error(final JobType type,final ServiceMethod method,final ErrorCode error)
			{
			}
		}));
		final LongId retrieved=new LongId();
		persistedObjectRetriever.get(retrieved,ServiceImplementation.key);
		assertEquals(expected,retrieved);
	}

	private static class ServiceImplementation extends Service
	{
		final static String key="x";

		ServiceImplementation(final PersistedObjectRetriever persistedObjectRetriever)
		{
			super(null,persistedObjectRetriever);
		}

		@Override
		protected ServiceMethod[] methodNames()
		{
			return new ServiceMethod[0];
		}

		@Override
		protected com.kareebo.contacts.client.jobs.Service.Functor[] functors()
		{
			return new com.kareebo.contacts.client.jobs.Service.Functor[0];
		}

		void run(final TBase payload,final Enqueuers enqueuers) throws Exception
		{
			new Functor<TBase>()
			{
				@Override
				protected void runInternal(final PersistedObjectRetriever persistedObjectRetriever,final TBase payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
				{
					persistedObjectRetriever.put(key,payload);
				}
			}.run(payload,enqueuers);
		}
	}
}