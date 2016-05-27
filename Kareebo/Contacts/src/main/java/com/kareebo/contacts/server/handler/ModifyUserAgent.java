package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.SignatureBuffer;
import com.kareebo.contacts.thrift.UserAgent;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import javax.annotation.Nonnull;

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
	public ModifyUserAgent(final @Nonnull DataStore<Long,User> dataStore)
	{
		super(dataStore);
	}

	@Override
	public void modifyUserAgent1(final @Nonnull UserAgent userAgent,final @Nonnull SignatureBuffer signature,final @Nonnull Future<Void> future)
	{
		verify(userAgent,signature,new Reply<>(future),(user,client)->{
			client.setUserAgent(TypeConverter.convert(userAgent));
			clientDBAccessor.put(client);
		});
	}
}
