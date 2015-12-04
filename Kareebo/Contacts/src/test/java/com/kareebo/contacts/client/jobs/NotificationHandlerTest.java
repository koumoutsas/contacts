package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.Notification;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TSerializer;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link NotificationHandler}
 */
public class NotificationHandlerTest
{
	final private EnqueuerImplementation enqueuer=new EnqueuerImplementation();
	final private NotificationHandler notificationHandler=new NotificationHandler(new ServiceDispatcherImplementation(enqueuer));

	@Test
	public void testHandle() throws Exception
	{
		final ServiceMethod method=new ServiceMethod(ServiceImplementation.class.getSimpleName(),"");
		final long id=0;
		notificationHandler.handle(new TSerializer().serialize(new Notification(method,id)));
		assertTrue(enqueuer.hasJob(JobType.Protocol,method,new LongId(id)));
	}
}
