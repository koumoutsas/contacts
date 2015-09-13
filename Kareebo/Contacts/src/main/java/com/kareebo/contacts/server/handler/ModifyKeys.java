package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.BasePlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.PublicKeys;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import java.security.NoSuchAlgorithmException;

/**
 * Key modification operation
 */
public class ModifyKeys extends SignatureVerifier implements com.kareebo.contacts.thrift.ModifyKeys.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(ModifyKeys.class.getName());

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	public ModifyKeys(final DataStore<Long,User> dataStore)
	{
		super(dataStore);
	}

	@Override
	/**
	 * The client sends the new keys signed with the old keys
	 */
	public void modifyKeys1(final PublicKeys newPublicKeys,final SignatureBuffer signature,final Future<Void>
		                                                                                       future)
	{
		verify(new BasePlaintextSerializer<>(newPublicKeys),signature,new Reply<>(future),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
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
			}
		});
	}
}
