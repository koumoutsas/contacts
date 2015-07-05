package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PublicKeysPlaintextSerializer;
import com.kareebo.contacts.common.PublicKeys;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.InvalidArgument;
import com.kareebo.contacts.thrift.Signature;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import java.security.NoSuchAlgorithmException;

/**
 * Key modification operation
 */
public class ModifyKeys extends SignatureVerifier implements com.kareebo.contacts.thrift.ModifyKeys.AsyncIface
{
	private com.kareebo.contacts.server.gora.PublicKeys newPublicKeys;

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	ModifyKeys(final DataStore<Long,User> dataStore)
	{
		super(dataStore);
	}

	@Override
	void afterVerification(final Client client)
	{
		client.setKeys(newPublicKeys);
	}

	@Override
	/**
	 * The client sends the new keys signed with the old keys
	 */
	public void modifyKeys1(final PublicKeys newPublicKeys,final Signature signature,final Future<Void> future)
	{
		try
		{
			this.newPublicKeys=TypeConverter.convert(newPublicKeys);
		}
		catch(NoSuchAlgorithmException e)
		{
			future.setFailure(new InvalidArgument());
			return;
		}
		verify(new PublicKeysPlaintextSerializer(newPublicKeys),signature,future);
	}
}