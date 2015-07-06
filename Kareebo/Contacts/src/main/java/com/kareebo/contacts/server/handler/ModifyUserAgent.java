package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.UserAgentPlaintextSerializer;
import com.kareebo.contacts.common.UserAgent;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.Signature;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

/**
 * User agent modification operation
 */
public class ModifyUserAgent extends SignatureVerifier implements com.kareebo.contacts.thrift.ModifyUserAgent.AsyncIface
{
	/**
	 * Stores the user agent for modifying the client state on success
	 */
	private UserAgent userAgent;

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	ModifyUserAgent(final DataStore<Long,User> dataStore)
	{
		super(dataStore);
	}

	@Override
	void afterVerification(final User user,final Client client)
	{
		client.setUserAgent(TypeConverter.convert(userAgent));
	}

	@Override
	public void modifyUserAgent1(final UserAgent userAgent,final Signature signature,final Future<Void> future)
	{
		this.userAgent=userAgent;
		verify(new UserAgentPlaintextSerializer(userAgent),signature,future);
	}
}
