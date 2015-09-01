package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.UserAgentPlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

/**
 * User agent modification operation
 */
public class ModifyUserAgent extends SignatureVerifier implements com.kareebo.contacts.thrift.ModifyUserAgent.AsyncIface
{
	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	public ModifyUserAgent(final DataStore<Long,User> dataStore)
	{
		super(dataStore);
	}

	@Override
	public void modifyUserAgent1(final UserAgent userAgent,final SignatureBuffer signature,final Future<Void> future)
	{
		verify(new UserAgentPlaintextSerializer(userAgent),signature,new Reply(future),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				client.setUserAgent(TypeConverter.convert(userAgent));
				clientDBAccessor.put(client);
			}
		});
	}
}
