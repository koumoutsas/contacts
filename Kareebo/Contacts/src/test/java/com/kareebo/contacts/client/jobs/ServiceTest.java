package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.UserAgent;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Service}
 */
public class ServiceTest
{
	final private ServiceMethod method=new ServiceMethod("","");
	final private EnqueuerImplementation enqueuer=new EnqueuerImplementation();
	final private Enqueuers enqueuers=new Enqueuers(JobType.Protocol,enqueuer,enqueuer);
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testRun() throws Exception
	{
		final UserAgent expected=new UserAgent("a","b");
		new ServiceImplementation().run(method,expected,enqueuers);
		assertTrue(enqueuer.hasJob(JobType.Protocol,method,expected));
	}

	@Test
	public void testNoSuchMethod() throws Exception
	{
		thrown.expect(Service.NoSuchMethod.class);
		new ServiceImplementation(new Service.NoSuchMethod()).run(method,null,enqueuers);
	}

	@Test
	public void testClassCastException() throws Exception
	{
		thrown.expect(Service.NoSuchMethod.class);
		new ServiceImplementation(new ClassCastException()).run(method,null,enqueuers);
	}

	@Test
	public void testException() throws Exception
	{
		thrown.expect(Service.ExecutionFailed.class);
		new ServiceImplementation(new Exception()).run(method,null,enqueuers);
	}
}