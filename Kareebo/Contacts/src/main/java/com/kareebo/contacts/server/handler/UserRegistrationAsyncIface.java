package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.StringPlaintextSerializer;
import com.kareebo.contacts.common.PublicKeys;
import com.kareebo.contacts.common.UserAgent;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.IdPair;
import com.kareebo.contacts.thrift.InvalidArgument;
import com.kareebo.contacts.thrift.Signature;
import com.kareebo.contacts.thrift.UserRegistration;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * User registration operation
 */
public class UserRegistrationAsyncIface extends SignatureVerifier implements UserRegistration.AsyncIface
{
	final private DataStore<RegistrationCodeId,RegistrationCode> registrationCodeDataStore;
	final private DataStore<Long,User> userDataStore;
	final private long expirationTime;
	private RegistrationCode registrationCode;

	/**
	 * Constructor from datastores
	 *
	 * @param clientDataStore           The client datastore
	 * @param registrationCodeDataStore The registration code datastore
	 * @param expirationTime            The expiration time of a registration code in seconds
	 */
	UserRegistrationAsyncIface(final DataStore<Long,User> clientDataStore,final DataStore<RegistrationCodeId,
		                                                                                     RegistrationCode> registrationCodeDataStore,final long expirationTime)
	{
		super(clientDataStore);
		this.registrationCodeDataStore=registrationCodeDataStore;
		this.userDataStore=clientDataStore;
		this.expirationTime=expirationTime;
	}

	@Override
	public void userRegistration1(final String code,final Signature signature,final UserAgent userAgent,final PublicKeys publicKeys,final Future<Void> future)
	{
		final IdPair idPair=signature.getIds();
		final RegistrationCodeId registrationCodeId=new RegistrationCodeId();
		registrationCodeId.setUserId(idPair.getUserId());
		registrationCodeId.setClientId(idPair.getClientId());
		registrationCodeId.setCode(code);
		registrationCode=registrationCodeDataStore.get(registrationCodeId);
		if(registrationCode==null)
		{
			future.setFailure(new InvalidArgument());
			return;
		}
		if(TimeUnit.MILLISECONDS.toSeconds(new Date().getTime()-registrationCode.getCreated())>expirationTime)
		{
			future.setFailure(new InvalidArgument());
			deleteRegistrationCode();
		}
		else
		{
			final User user=new User();
			final long userId=idPair.getUserId();
			user.setId(userId);
			user.setClients(new HashMap<CharSequence,Client>());
			userDataStore.put(idPair.getUserId(),user);
			final Client client=new Client();
			client.setUserAgent(TypeConverter.convert(userAgent));
			client.setContacts(new ArrayList<HashedContact>());
			try
			{
				client.setKeys(TypeConverter.convert(publicKeys));
				put(idPair,client);
				verify(new StringPlaintextSerializer(code),signature,future);
			}
			catch(NoSuchAlgorithmException|InvalidArgument e)
			{
				userDataStore.delete(userId);
				future.setFailure(new InvalidArgument());
			}
		}
	}

	private void deleteRegistrationCode()
	{
		registrationCodeDataStore.delete(registrationCode.getId());
		registrationCodeDataStore.close();
	}

	@Override
	void afterVerification(final Client client)
	{
		deleteRegistrationCode();
	}
}
