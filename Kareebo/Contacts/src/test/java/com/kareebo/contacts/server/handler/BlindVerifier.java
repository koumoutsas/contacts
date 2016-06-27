package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;

import javax.annotation.Nonnull;

/**
 * Convenience delegate for verifying signatures blindly
 */
class BlindVerifier
{
	private final ClientDBAccessor clientDBAccessor;

	BlindVerifier(@Nonnull final ClientDBAccessor clientDBAccessor)
	{
		this.clientDBAccessor=clientDBAccessor;
	}

	void verify(@Nonnull final SignatureBuffer signature,@Nonnull final Reply<?> reply,@Nonnull final SignatureVerifier.After after)
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
			after.run(clientDBAccessor.user,client);
		}
		catch(FailedOperation failedOperation)
		{
			reply.setFailure(failedOperation);
			return;
		}
		clientDBAccessor.close();
		reply.setReply();
	}
}
