package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.PendingNotification;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.UserAgent;
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
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for {@link ClientNotifier}
 */
public class ClientNotifierTest
{
	private final long deviceToken=94;
	private final List<Long> deviceTokens=Arrays.asList(deviceToken,deviceToken+1);
	final private UserAgent expected=new UserAgent("a","b");
	@Rule
	public ExpectedException thrown=ExpectedException.none();
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
		clientNotifier.put(deviceToken,expected);
		assertEquals(1,notifierBackend.sentNotifications.size());
		final Long notificationId=notifierBackend.sentNotifications.get(deviceToken);
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
	public void testPut() throws Exception
	{
		clientNotifier.put(deviceTokens,expected);
		assertEquals(deviceTokens.size(),notifierBackend.sentNotifications.size());
		for(final Long deviceToken2 : deviceTokens)
		{
			final Long notificationId=notifierBackend.sentNotifications.get(deviceToken2);
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
		clientNotifier.put(deviceTokens,expected);
		assertEquals(0,notifierBackend.sentNotifications.size());
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
		clientNotifier.put(deviceTokens,o);
		assertEquals(0,notifierBackend.sentNotifications.size());
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testPutNotifierError() throws Exception
	{
		notifierBackend.fail=true;
		thrown.expect(FailedOperation.class);
		clientNotifier.put(deviceTokens,expected);
		assertEquals(0,notifierBackend.sentNotifications.size());
		assertFalse(datastore.hasBeenClosed());
	}

	@Test
	public void testGet() throws Exception
	{
		clientNotifier.put(deviceToken,expected);
		final UserAgent retrieved=new UserAgent();
		final Long notificationId=notifierBackend.sentNotifications.values().iterator().next();
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
		clientNotifier.put(deviceToken,expected);
		final UserAgent retrieved=new UserAgent()
		{
			@Override
			public void read(TProtocol var1) throws TException
			{
				throw new TException();
			}
		};
		final Long notificationId=notifierBackend.sentNotifications.values().iterator().next();
		thrown.expect(FailedOperation.class);
		clientNotifier.get(retrieved,notificationId);
		assertFalse(datastore.hasBeenClosed());
		assertNotNull(datastore.get(notificationId));
	}
}