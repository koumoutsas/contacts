package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * @param plaintext The plaintext serializer
	 * @param signature The signature
	 * @param reply     The reply future used to communicate the result
	 * @param after     The after hook
	 */
	void verify(final TBase plaintext,final SignatureBuffer signature,final Reply<?> reply,final After after)
	{
		final Client client;
		try
		{
			client=clientDBAccessor.get(signature.getClient());
		}
		catch(FailedOperation failedOperation)
		{
			reply.setFailure(failedOperation);
			return;
		}
		try
		{
			final ByteBuffer signatureBuffer=signature.bufferForBuffer();
			signatureBuffer.rewind();
			if(!Utils.verifySignature(client.getKeys().getVerification(),signatureBuffer,new TSerializer().serialize(plaintext)))
			{
				logger.error("Verification failure for "+client);
				reply.setFailure(new FailedOperation());
				return;
			}
			after.run(clientDBAccessor.user,client);
		}
		catch(NoSuchProviderException|NoSuchAlgorithmException|SignatureException|InvalidKeyException|InvalidKeySpecException e)
		{
			logger.error("Verification failure with exception",e);
			reply.setFailure(new FailedOperation());
			return;
		}
		catch(FailedOperation failedOperation)
		{
			reply.setFailure(failedOperation);
			return;
		}
		catch(TException e)
		{
			logger.error("Serialization error",e);
			reply.setFailure(new FailedOperation());
			return;
		}
		clientDBAccessor.close();
		reply.setReply();
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
