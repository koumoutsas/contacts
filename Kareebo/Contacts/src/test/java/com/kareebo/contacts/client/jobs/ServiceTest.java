package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.ServiceMethod;
import com.kareebo.contacts.thrift.UserAgent;
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
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testRun() throws Exception
	{
		final UserAgent expected=new UserAgent("a","b");
		new ServiceImplementation().run(method,expected,enqueuer);
		assertTrue(enqueuer.job(method,expected));
	}

	@Test
	public void testNoSuchMethod() throws Exception
	{
		thrown.expect(Service.NoSuchMethod.class);
		new ServiceImplementation(new Service.NoSuchMethod()).run(method,null,enqueuer);
	}

	@Test
	public void testClassCastException() throws Exception
	{
		thrown.expect(Service.NoSuchMethod.class);
		new ServiceImplementation(new ClassCastException()).run(method,null,enqueuer);
	}

	@Test
	public void testException() throws Exception
	{
		thrown.expect(Service.ExecutionFailed.class);
		new ServiceImplementation(new Exception()).run(method,null,enqueuer);
	}
}