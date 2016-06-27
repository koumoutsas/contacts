package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.PendingNotification;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.UserAgent;
import com.kareebo.contacts.thrift.client.jobs.Notification;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TProtocol;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit test for {@link ClientNotifier}
 */
public class ClientNotifierTest
{
	@Rule
	final public ExpectedException thrown=ExpectedException.none();
	private final long deviceToken=94;
	private final List<Long> deviceTokens=Arrays.asList(deviceToken,deviceToken+1);
	final private UserAgent expected=new UserAgent("a","b");
	final private ServiceMethod expectedMethod=new ServiceMethod("a","b");
	final private NotificationObject notificationObject=new NotificationObject(expectedMethod,expected);
	private MemStore<Long,PendingNotification> datastore;
	private Notifier notifierBackend;
	private ClientNotifier clientNotifier;

	@Before
	public void setUp() throws Exception
	{
		datastore=(MemStore<Long,PendingNotification>)DataStoreFactory.getDataStore(Long.class,PendingNotification.class,new Configuration());
		notifierBackend=new Notifier();
		clientNotifier=new ClientNotifier(notifierBackend,datastore);
	}

	@Test
	public void testPutSingle() throws Exception
	{
		clientNotifier.put(deviceToken,notificationObject);
		assertEquals(1,notifierBackend.size());
		final Notification notification=notifierBackend.get(deviceToken);
		assertEquals(expectedMethod,notification.getMethod());
		final Long notificationId=notification.getId();
		assertNotNull(notificationId);
		assertTrue(datastore.hasBeenClosed());
		final PendingNotification pendingNotification=datastore.get(notificationId);
		assertNotNull(pendingNotification);
		assertEquals(notificationId,pendingNotification.getId());
		final ByteBuffer payload=pendingNotification.getPayload();
		payload.rewind();
		final ByteBuffer expectedPayload=ByteBuffer.wrap(new TSerializer().serialize(expected));
		expectedPayload.mark();
		assertEquals(expectedPayload,payload);
	}

	@Test
	public void testPutSizeExceeds() throws Exception
	{
		final StringBuilder tmp=new StringBuilder();
		for(int i=0;i<2*1024;++i)
		{
			tmp.append('0');
		}
		thrown.expect(FailedOperation.class);
		clientNotifier.put(deviceToken,new NotificationObject(new ServiceMethod(tmp.toString(),"1"),expected));
		assertEquals(0,notifierBackend.size());
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testPut() throws Exception
	{
		clientNotifier.put(deviceTokens,notificationObject);
		assertEquals(deviceTokens.size(),notifierBackend.size());
		for(final Long deviceToken2 : deviceTokens)
		{
			final Notification notification=notifierBackend.get(deviceToken2);
			assertEquals(expectedMethod,notification.getMethod());
			final Long notificationId=notification.getId();
			assertNotNull(notificationId);
			assertTrue(datastore.hasBeenClosed());
			final PendingNotification pendingNotification=datastore.get(notificationId);
			assertNotNull(pendingNotification);
			assertEquals(notificationId,pendingNotification.getId());
			final ByteBuffer payload=pendingNotification.getPayload();
			payload.rewind();
			final ByteBuffer expectedPayload=ByteBuffer.wrap(new TSerializer().serialize(expected));
			expectedPayload.mark();
			assertEquals(expectedPayload,payload);
		}
	}

	@Test
	public void testPutMap() throws Exception
	{
		final Map<Long,NotificationObject> expectedPayloads=new HashMap<>(deviceTokens.size());
		for(final Long deviceToken2 : deviceTokens)
		{
			expectedPayloads.put(deviceToken2,new NotificationObject(expectedMethod,new UserAgent("a",TypeConverter.convert
				                                                                                                        (deviceToken).toString())));
		}
		clientNotifier.put(expectedPayloads);
		assertEquals(deviceTokens.size(),notifierBackend.size());
		for(final Long deviceToken2 : deviceTokens)
		{
			final Notification notification=notifierBackend.get(deviceToken2);
			assertEquals(expectedMethod,notification.getMethod());
			final Long notificationId=notification.getId();
			assertNotNull(notificationId);
			assertTrue(datastore.hasBeenClosed());
			final PendingNotification pendingNotification=datastore.get(notificationId);
			assertNotNull(pendingNotification);
			assertEquals(notificationId,pendingNotification.getId());
			final ByteBuffer payload=pendingNotification.getPayload();
			payload.rewind();
			final ByteBuffer expectedPayload=expectedPayloads.get(deviceToken2).getPayload();
			assertEquals(expectedPayload,payload);
		}
	}

	@Test
	public void testPutMapSerializationError() throws Exception
	{
		final UserAgent o=new UserAgent()
		{
			@Override
			public void write(TProtocol var1) throws TException
			{
				throw new TException();
			}
		};
		o.setPlatform("");
		o.setVersion("");
		final Map<Long,NotificationObject> expectedPayloads=new HashMap<>(deviceTokens.size());
		for(final Long deviceToken2 : deviceTokens)
		{
			expectedPayloads.put(deviceToken2,new NotificationObject(expectedMethod,o));
		}
		thrown.expect(FailedOperation.class);
		clientNotifier.put(expectedPayloads);
		assertEquals(0,notifierBackend.size());
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testPutAlreadyExists() throws Exception
	{
		final long useId=9;
		final PendingNotification usePendingNotification=new PendingNotification();
		usePendingNotification.setId(useId);
		final ByteBuffer buffer=ByteBuffer.wrap("".getBytes());
		buffer.mark();
		usePendingNotification.setPayload(buffer);
		datastore.put(useId,usePendingNotification);
		datastore.useId=useId;
		thrown.expect(FailedOperation.class);
		clientNotifier.put(deviceTokens,notificationObject);
		assertEquals(0,notifierBackend.size());
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testPutSerializationError() throws Exception
	{
		final UserAgent o=new UserAgent()
		{
			@Override
			public void write(TProtocol var1) throws TException
			{
				throw new TException();
			}
		};
		o.setPlatform("");
		o.setVersion("");
		thrown.expect(FailedOperation.class);
		clientNotifier.put(deviceTokens,new NotificationObject(expectedMethod,o));
		assertEquals(0,notifierBackend.size());
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testPutNotifierError() throws Exception
	{
		notifierBackend.fail=true;
		thrown.expect(FailedOperation.class);
		clientNotifier.put(deviceTokens,notificationObject);
		assertEquals(0,notifierBackend.size());
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testGet() throws Exception
	{
		clientNotifier.put(deviceToken,notificationObject);
		final UserAgent retrieved=new UserAgent();
		final Notification notification=notifierBackend.getFirst();
		assertEquals(expectedMethod,notification.getMethod());
		final Long notificationId=notification.getId();
		clientNotifier.get(retrieved,notificationId);
		assertEquals(expected,retrieved);
		assertTrue(datastore.hasBeenClosed());
		assertNull(datastore.get(notificationId));
	}

	@Test
	public void testGetNonExistent() throws Exception
	{
		thrown.expect(FailedOperation.class);
		clientNotifier.get(new UserAgent(),9);
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testGetDeserializationError() throws Exception
	{
		clientNotifier.put(deviceToken,notificationObject);
		final UserAgent retrieved=new UserAgent()
		{
			@Override
			public void read(TProtocol var1) throws TException
			{
				throw new TException();
			}
		};
		final Notification notification=notifierBackend.getFirst();
		assertEquals(expectedMethod,notification.getMethod());
		final Long notificationId=notification.getId();
		thrown.expect(FailedOperation.class);
		clientNotifier.get(retrieved,notificationId);
		assertFalse(datastore.hasBeenClosed());
		assertNotNull(datastore.get(notificationId));
	}
}