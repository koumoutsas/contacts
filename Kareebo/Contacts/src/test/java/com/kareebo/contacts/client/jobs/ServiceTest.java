package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.UserAgent;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Service}
 */
public class ServiceTest
{
	final private EnqueuerImplementation enqueuer=new EnqueuerImplementation();
	final private Enqueuers enqueuers=new Enqueuers(JobType.Protocol,enqueuer,enqueuer);
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testRun() throws Exception
	{
		final UserAgent expected=new UserAgent("a","b");
		new ServiceImplementation().run(ServiceImplementation.method,expected,enqueuers);
		assertTrue(enqueuer.hasJob(JobType.Protocol,ServiceImplementation.method,expected));
	}

	@Test
	public void testNoSuchMethod() throws Exception
	{
		thrown.expect(Service.NoSuchMethod.class);
		new ServiceImplementation().run(new ServiceMethod("",""),null,enqueuers);
	}

	@Test
	public void testClassCastException() throws Exception
	{
		thrown.expect(Service.NoSuchMethod.class);
		new ServiceImplementation(new ClassCastException()).run(ServiceImplementation.method,null,enqueuers);
	}

	@Test
	public void testException() throws Exception
	{
		thrown.expect(Service.ExecutionFailed.class);
		new ServiceImplementation(new Exception()).run(ServiceImplementation.method,null,enqueuers);
	}

	@Test
	public void testMismatchedFunctors() throws Exception
	{
		thrown.expect(IllegalArgumentException.class);
		new Service(null)
		{
			@Override
			protected ServiceMethod[] methodNames()
			{
				return new ServiceMethod[]{new ServiceMethod("","")};
			}

			@Override
			protected Functor[] functors()
			{
				return new Functor[]{new Functor()
				{
					@Override
					public void run(final TBase payload,final Enqueuers enqueuers) throws Exception
					{
					}
				},new Functor()
				{
					@Override
					public void run(final TBase payload,final Enqueuers enqueuers) throws Exception
					{
					}
				}};
			}
		};
	}

	@Test
	public void testDuplicateMethods() throws Exception
	{
		thrown.expect(IllegalArgumentException.class);
		new Service(null)
		{
			@Override
			protected ServiceMethod[] methodNames()
			{
				return new ServiceMethod[]{new ServiceMethod("","1"),new ServiceMethod("","1")};
			}

			@Override
			protected Functor[] functors()
			{
				return new Functor[]{new Functor()
				{
					@Override
					public void run(final TBase payload,final Enqueuers enqueuers) throws Exception
					{
					}
				},new Functor()
				{
					@Override
					public void run(final TBase payload,final Enqueuers enqueuers) throws Exception
					{
					}
				}};
			}
		};
	}
}