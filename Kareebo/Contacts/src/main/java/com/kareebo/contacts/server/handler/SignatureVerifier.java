package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.InvalidArgument;
import com.kareebo.contacts.thrift.Signature;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * Base class for all services that verify a signature and modify the client state upon successful verification
 */
abstract class SignatureVerifier extends ClientDBAccessor
{
	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	SignatureVerifier(final DataStore<Long,User> dataStore)
	{
		super(dataStore);
	}

	/**
	 * Verify the signature
	 *
	 * @param plaintextSerializer The plaintext serializer
	 * @param signature The signature
	 * @param future    The future used to communicate the result
	 */
	void verify(final PlaintextSerializer plaintextSerializer,final Signature signature,final Future<Void> future)
	{
		final Client client;
		try
		{
			client=get(signature.getIds());
		}
		catch(InvalidArgument invalidArgument)
		{
			future.setFailure(invalidArgument);
			return;
		}
		try
		{
			final ByteBuffer signatureBuffer=signature.bufferForSignature();
			signatureBuffer.rewind();
			if(!Utils.verifySignature(client.getKeys().getVerification(),signatureBuffer,plaintextSerializer))
			{
				future.setFailure(new InvalidArgument());
				return;
			}
			afterVerification(client);
		}
		catch(NoSuchProviderException|NoSuchAlgorithmException|SignatureException|InvalidKeyException|InvalidKeySpecException e)
		{
			future.setFailure(new InvalidArgument());
			return;
		}
		catch(InvalidArgument invalidArgument)
		{
			future.setFailure(invalidArgument);
			return;
		}
		put(client);
		close();
		future.setResult(null);
	}

	/**
	 * Abstract method for modifying the state of the client after a successful verification
	 *
	 * @param client The client
	 */
	abstract void afterVerification(final Client client) throws InvalidArgument;
}
