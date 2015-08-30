package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
abstract class SignatureVerifier
{
	private static final Logger logger=LoggerFactory.getLogger(SignatureVerifier.class.getName());
	final ClientDBAccessor clientDBAccessor;

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	SignatureVerifier(final DataStore<Long,User> dataStore)
	{
		clientDBAccessor=new ClientDBAccessor(dataStore);
	}

	/**
	 * Verify the signature
	 *
	 * @param plaintextSerializer The plaintext serializer
	 * @param signature           The signature
	 * @param future              The future used to communicate the result
	 * @param after               The after hook
	 */
	void verify(final PlaintextSerializer plaintextSerializer,final SignatureBuffer signature,final Future<?> future,final After after)
	{
		final Client client;
		try
		{
			client=clientDBAccessor.get(signature.getClient());
		}
		catch(FailedOperation failedOperation)
		{
			future.setFailure(failedOperation);
			return;
		}
		try
		{
			final ByteBuffer signatureBuffer=signature.bufferForBuffer();
			signatureBuffer.rewind();
			if(!Utils.verifySignature(client.getKeys().getVerification(),signatureBuffer,plaintextSerializer))
			{
				logger.error("Verification failure for "+client.toString());
				future.setFailure(new FailedOperation());
				return;
			}
			after.run(clientDBAccessor.user,client);
		}
		catch(NoSuchProviderException|NoSuchAlgorithmException|SignatureException|InvalidKeyException|InvalidKeySpecException e)
		{
			logger.error("Verification failure with exception",e);
			future.setFailure(new FailedOperation());
			return;
		}
		catch(FailedOperation failedOperation)
		{
			future.setFailure(failedOperation);
			return;
		}
		clientDBAccessor.close();
		future.setResult(null);
	}

	/**
	 * Closure for the operations to be executed after a successful verification
	 */
	interface After
	{
		/**
		 * @param user   The user
		 * @param client The client
		 */
		void run(final User user,final Client client) throws FailedOperation;
	}
}
