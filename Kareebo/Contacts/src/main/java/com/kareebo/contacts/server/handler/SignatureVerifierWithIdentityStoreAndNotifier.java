package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.LongId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.vertx.java.core.Future;

import javax.annotation.Nonnull;
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
	SignatureVerifierWithIdentityStoreAndNotifier(final @Nonnull DataStore<Long,User> userDataStore,final @Nonnull DataStore<ByteBuffer,HashIdentity> identityDatastore,final @Nonnull ClientNotifier clientNotifier)
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
	<T extends TBase> void forward(final @Nonnull T t,final @Nonnull LongId id,final @Nonnull SignatureBuffer signature,final @Nonnull Future<T> future)
	{
		verify(id,signature,new Reply<>(future,t),(user,client)->clientNotifier.get(t,id.getId()));
	}

	void notifyClient(final long deviceToken,final @Nonnull NotificationObject payload) throws FailedOperation
	{
		clientNotifier.put(deviceToken,payload);
	}

	void notifyClients(final @Nonnull List<Long> deviceTokens,final @Nonnull NotificationObject payload) throws FailedOperation
	{
		clientNotifier.put(deviceTokens,payload);
	}

	void notifyClients(final @Nonnull Map<Long,NotificationObject> notifications) throws FailedOperation
	{
		clientNotifier.put(notifications);
	}

	protected void get(final @Nonnull TBase object,final long notificationId) throws FailedOperation
	{
		clientNotifier.get(object,notificationId);
	}
}
