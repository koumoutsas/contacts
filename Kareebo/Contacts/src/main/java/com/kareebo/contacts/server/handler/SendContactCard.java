package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Server-side service implementation of the send contact card operation
 */
public class SendContactCard extends SignatureVerifierWithIdentityStore implements com.kareebo.contacts.thrift.SendContactCard.AsyncIface
{
	private final ClientNotifier clientNotifier;

	/**
	 * Constructor from a datastore
	 *
	 * @param userDataStore     The user datastore
	 * @param identityDatastore The identity datastore
	 * @param clientNotifier    The client notifier interface
	 */
	SendContactCard(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
	{
		super(userDataStore,identityDatastore);
		this.clientNotifier=clientNotifier;
	}

	@Override
	public void sendContactCard1(final HashBuffer u,final SignatureBuffer signature,final Future<Void> future)
	{
	}

	@Override
	public void sendContactCard2(final LongId id,final SignatureBuffer signature,final Future<EncryptionKeys> future)
	{
	}

	@Override
	public void sendContactCard3(final Set<EncryptedBufferSigned> encryptedBuffers,final Future<Void> future)
	{
	}

	@Override
	public void sendContactCard4(final LongId id,final SignatureBuffer signature,final Future<EncryptedBuffersSignedWithVerificationKey> future)
	{
	}
}
