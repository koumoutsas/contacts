package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.PublicKeys;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import javax.annotation.Nonnull;
import java.security.NoSuchAlgorithmException;

/**
 * Server-side implementation of the modify keys operation
 */
public class ModifyKeys extends SignatureVerifier implements com.kareebo.contacts.thrift.ModifyKeys.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(ModifyKeys.class.getName());

	public ModifyKeys(final @Nonnull Configuration configuration)
	{
		super(configuration.getUserDataStore());
	}

	@Override
	/**
	 * The client sends the new keys signed with the old keys
	 */
	public void modifyKeys1(final @Nonnull PublicKeys newPublicKeys,final @Nonnull SignatureBuffer signature,final @Nonnull Future<Void>
		                                                                                                         future)
	{
		verify(newPublicKeys,signature,new Reply<>(future),(user,client)->
		{
			try
			{
				client.setKeys(TypeConverter.convert(newPublicKeys));
				clientDBAccessor.put(client);
			}
			catch(NoSuchAlgorithmException e)
			{
				logger.error("Invalid algorithm",e);
				throw new FailedOperation();
			}
		});
	}
}
