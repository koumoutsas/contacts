package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Extension of {@link SignatureVerifierWithIdentityStore} with {@link ClientNotifier}
 */
abstract class SignatureVerifierWithIdentityStoreAndNotifier extends SignatureVerifierWithIdentityStore
{
	private final ClientNotifier clientNotifier;

	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The user datastore
	 * @param identityDatastore The identity datastore
	 * @param clientNotifier    The client notifier interface
	 */
	SignatureVerifierWithIdentityStoreAndNotifier(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
	{
		super(userDataStore,identityDatastore);
		this.clientNotifier=clientNotifier;
	}

	/**
	 * Forward the payload of a notification
	 *
	 * @param t         The notification payload object. We need to pass it explicitly
	 * @param id        The notification id
	 * @param signature The signature of id
	 * @param future    The future that is notified of the result of the operation
	 */
	protected <T extends TBase> void forward(final T t,final LongId id,final SignatureBuffer signature,final Future<T> future)
	{
		verify(id,signature,new Reply<>(future,t),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				clientNotifier.get(t,id.getId());
			}
		});
	}

	protected void notifyClient(final long deviceToken,final NotificationObject payload) throws FailedOperation
	{
		clientNotifier.put(deviceToken,payload);
	}

	protected void notifyClients(final List<Long> deviceTokens,final NotificationObject payload) throws FailedOperation
	{
		clientNotifier.put(deviceTokens,payload);
	}

	protected void notifyClients(final Map<Long,NotificationObject> notifications) throws FailedOperation
	{
		clientNotifier.put(notifications);
	}

	protected void get(final TBase object,final long notificationId) throws FailedOperation
	{
		clientNotifier.get(object,notificationId);
	}
}
