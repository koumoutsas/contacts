package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.server.gora.PendingNotification;
import com.kareebo.contacts.thrift.FailedOperation;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.*;

/**
 * Stores and retrieves pending messages to clients and interfaces with the push notification system
 */
class ClientNotifier
{
	private static final Logger logger=LoggerFactory.getLogger(ClientNotifier.class.getName());
	private final ClientNotifierBackend clientNotifierBackend;
	private final DataStore<Long,PendingNotification> pendingNotificationDataStore;

	/**
	 * Constructor
	 *
	 * @param clientNotifierBackend        The interface to the push notifications backend
	 * @param pendingNotificationDataStore The datastore for pending notifications
	 */
	ClientNotifier(final ClientNotifierBackend clientNotifierBackend,final DataStore<Long,PendingNotification> pendingNotificationDataStore)
	{
		this.clientNotifierBackend=clientNotifierBackend;
		this.pendingNotificationDataStore=pendingNotificationDataStore;
	}

	/**
	 * Put a notification payload in the datastore and send a notification to the client with a unique id
	 *
	 * @param deviceToken The device token used by the push notification server to identify the client
	 * @param object      The object to be stored. It is a subclass of TBase
	 * @throws FailedOperation If either a unique id cannot be generated, the Thrift serialization fails, or the notification cannot be sent to
	 *                         the client
	 */
	void put(final long deviceToken,final TBase object) throws FailedOperation
	{
		put(Collections.singletonList(deviceToken),object);
	}

	/**
	 * Put a notification payload in the datastore and send a notification to multiple clients
	 *
	 * @param deviceTokens The device tokens used by the push notification server to identify the client
	 * @param object       The object to be stored. It is a subclass of TBase
	 * @throws FailedOperation If either a unique id cannot be generated, the Thrift serialization fails, or the notification cannot be sent to
	 *                         the client
	 */
	void put(final List<Long> deviceTokens,final TBase object) throws FailedOperation
	{
		ByteBuffer payload;
		try
		{
			payload=ByteBuffer.wrap(new TSerializer().serialize(object));
			payload.mark();
		}
		catch(TException e)
		{
			logger.error("Failed serialization",e);
			throw new FailedOperation();
		}
		final Map<Long,ByteBuffer> notificationsInternal=new HashMap<>(deviceTokens.size());
		for(final Long deviceToken : deviceTokens)
		{
			notificationsInternal.put(deviceToken,payload);
		}
		putInternal(notificationsInternal);
	}

	private void putInternal(final Map<Long,ByteBuffer> notifications) throws FailedOperation
	{
		final List<AbstractMap.SimpleImmutableEntry<Long,Long>> notificationIds=new ArrayList<>(notifications.size());
		for(final Long deviceToken : notifications.keySet())
		{
			final long notificationId=new SecureRandom().nextLong();
			if(pendingNotificationDataStore.get(notificationId)!=null)
			{
				logger.error("Notification id "+notificationId+" already exists");
				throw new FailedOperation();
			}
			notificationIds.add(new AbstractMap.SimpleImmutableEntry<>(deviceToken,notificationId));
		}
		for(final AbstractMap.SimpleImmutableEntry<Long,Long> notification : notificationIds)
		{
			final PendingNotification pendingNotification=new PendingNotification();
			pendingNotification.setPayload(notifications.get(notification.getKey()));
			final Long notificationId=notification.getValue();
			pendingNotification.setId(notificationId);
			pendingNotificationDataStore.put(notificationId,pendingNotification);
			clientNotifierBackend.notify(notification.getKey(),notificationId);
		}
		pendingNotificationDataStore.close();
	}

	/**
	 * Put a set of notification payloads in the datastore and send the notifications to multiple clients
	 *
	 * @param notifications A map from device tokens used by the push notification server to identify the client to objects to be sent
	 * @throws FailedOperation If either a unique id cannot be generated, the Thrift serialization fails, or the notification cannot be sent to
	 *                         the client
	 */
	void put(final Map<Long,TBase> notifications) throws FailedOperation
	{
		final Map<Long,ByteBuffer> notificationsInternal=new HashMap<>(notifications.size());
		for(final Map.Entry<Long,TBase> entry : notifications.entrySet())
		{
			ByteBuffer payload;
			try
			{
				payload=ByteBuffer.wrap(new TSerializer().serialize(entry.getValue()));
				payload.mark();
			}
			catch(TException e)
			{
				logger.error("Failed serialization",e);
				throw new FailedOperation();
			}
			notificationsInternal.put(entry.getKey(),payload);
		}
		putInternal(notificationsInternal);
	}

	/**
	 * Get a pending notification payload, based on its unique id and remove it from the datastore
	 *
	 * @param object         The TBase object into which the payload is to be serialized
	 * @param notificationId The unique id that has been generated by {@link #put(long,TBase)}
	 * @throws FailedOperation If the unique id cannot be found in the datastore or the Thrift deserialization failed
	 */
	void get(final TBase object,final long notificationId) throws FailedOperation
	{
		final PendingNotification pendingNotification=pendingNotificationDataStore.get(notificationId);
		if(pendingNotification==null)
		{
			logger.error("Unable to find notification id "+notificationId);
			throw new FailedOperation();
		}
		try
		{
			new TDeserializer().deserialize(object,Utils.getBytes(pendingNotification.getPayload()));
		}
		catch(TException e)
		{
			logger.error("Failed deserialization",e);
			throw new FailedOperation();
		}
		pendingNotificationDataStore.delete(notificationId);
		pendingNotificationDataStore.close();
	}
}
