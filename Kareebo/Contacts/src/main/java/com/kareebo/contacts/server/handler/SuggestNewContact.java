package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Service implementation for updating the sending a contact card
 */
public class SuggestNewContact extends SignatureVerifierWithIdentityStoreAndNotifier implements com.kareebo.contacts.thrift.SuggestNewContact.AsyncIface
{
	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The user datastore
	 * @param identityDatastore The identity datastore
	 * @param clientNotifier    The client notifier interface
	 */
	SuggestNewContact(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity> identityDatastore,final ClientNotifier clientNotifier)
	{
		super(userDataStore,identityDatastore,clientNotifier);
	}

	@Override
	public void suggestNewContact1(final LongId id,final SignatureBuffer signature,final Future<EncryptionKeysWithHashBuffer> future)
	{
	}

	@Override
	public void suggestNewContact2(final Set<EncryptedBufferSigned> encryptedBuffers,final HashBuffer uB,final SignatureBuffer signature,final Future<Void> future)
	{
	}

	@Override
	public void suggestNewContact3(final LongId id,final SignatureBuffer signature,final Future<EncryptedBufferSignedWithVerificationKey> future)
	{
		forward(new EncryptedBufferSignedWithVerificationKey(),id,signature,future);
	}
}
