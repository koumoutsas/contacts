package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.UserAgent;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ServiceDispatcher}
 */
public class ServiceDispatcherTest
{
	final private ServiceMethod method=new ServiceMethod(ServiceImplementation.class.getSimpleName(),"");
	final private EnqueuerImplementation enqueuer=new EnqueuerImplementation();
	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Test
	public void testRun() throws Exception
	{
		final UserAgent userAgent=new UserAgent("a","b");
		new ServiceDispatcherImplementation(enqueuer).run(method,userAgent);
		assertTrue(enqueuer.hasJob(ServiceDispatcherImplementation.jobType(),method,userAgent));
	}

	@Test
	public void testRunNotificationId() throws Exception
	{
		final LongId id=new LongId(4);
		new ServiceDispatcherImplementation(enqueuer).run(method,id.getId());
		assertTrue(enqueuer.hasJob(ServiceDispatcherImplementation.jobType(),method,id));
	}

	@Test
	public void testClassNotFoundException() throws Exception
	{
		thrown.expect(ServiceDispatcher.NoSuchService.class);
		new ServiceDispatcherImplementation(enqueuer).run(new ServiceMethod("",""),null);
	}

	@Test
	public void testNoSuchMethodException() throws Exception
	{
		thrown.expect(Service.NoSuchMethod.class);
		new ServiceDispatcherImplementation(enqueuer,new Service.NoSuchMethod()).run(new ServiceMethod(method.getServiceName(),""),null);
	}

	@Test
	public void testInstantiationException() throws Exception
	{
		thrown.expect(ServiceDispatcher.NoSuchService.class);
		new ServiceDispatcherImplementation(enqueuer,new InstantiationException()).run(method,null);
	}

	@Test
	public void testIllegalAccessException() throws Exception
	{
		thrown.expect(ServiceDispatcher.NoSuchService.class);
		new ServiceDispatcherImplementation(enqueuer,new IllegalAccessException()).run(method,null);
	}

	@Test
	public void testInvocationTargetException() throws Exception
	{
		thrown.expect(ServiceDispatcher.NoSuchService.class);
		new ServiceDispatcherImplementation(enqueuer,new InvocationTargetException(null)).run(method,null);
	}
}