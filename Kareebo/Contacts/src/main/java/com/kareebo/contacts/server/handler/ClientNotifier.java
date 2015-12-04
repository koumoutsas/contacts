package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.server.gora.PendingNotification;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * @param object      The object to be stored, with the service method
	 * @throws FailedOperation If either a unique id cannot be generated, the Thrift serialization fails, or the notification cannot be sent to
	 *                         the client
	 */
	void put(final long deviceToken,final NotificationObject object) throws FailedOperation
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
	void put(final List<Long> deviceTokens,final NotificationObject object) throws FailedOperation
	{
		final Map<Long,NotificationObject> notificationsInternal=new HashMap<>(deviceTokens.size());
		for(final Long deviceToken : deviceTokens)
		{
			notificationsInternal.put(deviceToken,object);
		}
		put(notificationsInternal);
	}

	/**
	 * Put a set of notification payloads for different devices in the datastore and send the notification to the clients
	 *
	 * @param notifications The set of notifications as a map from device token to notification payload
	 * @throws FailedOperation If either a unique id cannot be generated, a Thrift serialization fails, or a notification cannot be sent to
	 *                         the client
	 */
	void put(final Map<Long,NotificationObject> notifications) throws FailedOperation
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
			final NotificationObject notificationObject=notifications.get(notification.getKey());
			try
			{
				pendingNotification.setPayload(notificationObject.getPayload());
				final Long notificationId=notification.getValue();
				pendingNotification.setId(notificationId);
				pendingNotificationDataStore.put(notificationId,pendingNotification);
				clientNotifierBackend.notify(notification.getKey(),new Notification(notificationObject.getMethod(),notificationId).payload());
			}
			catch(Notification.SizeLimitExceeded e)
			{
				logger.error("Notification exceeded maximum size",e);
				throw new FailedOperation();
			}
			catch(TException e)
			{
				logger.error("Notification serialization exception",e);
				throw new FailedOperation();
			}
		}
		pendingNotificationDataStore.close();
	}

	/**
	 * Get a pending notification payload, based on its unique id and remove it from the datastore
	 *
	 * @param object         The TBase object into which the payload is to be serialized
	 * @param notificationId The unique id that has been generated by {@link #put(long,NotificationObject)}
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

	private class Notification extends com.kareebo.contacts.thrift.client.jobs.Notification
	{
		///The push notification size limit for iOS 8 and above. Android limits are larger than 2k
		private static final int sizeLimit=2*1024;
		private final byte[] payloadBytes;

		/**
		 * Construct a notification from service method and notification id. The format of the payload is <service>.<method>:<id>
		 *
		 * @param method The notification method
		 * @param id     The notification id
		 * @throws SizeLimitExceeded If the payload is above the 2k limit
		 */
		Notification(final ServiceMethod method,final Long id) throws SizeLimitExceeded, TException
		{
			super(method,id);
			payloadBytes=new TSerializer().serialize(this);
			if(payloadBytes.length>sizeLimit)
			{
				throw new SizeLimitExceeded();
			}
		}

		byte[] payload()
		{
			return payloadBytes;
		}

		class SizeLimitExceeded extends Exception
		{
		}
	}
}
