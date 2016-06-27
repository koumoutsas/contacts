package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.PublicKeys;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.server.gora.UserAgent;
import com.kareebo.contacts.thrift.ClientId;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
class SignerWithSetupMainUser extends Signer
{
	final long deviceToken=0;
	final ClientId clientId=new ClientId(0,0);
	final DataStore<Long,User> userDataStore;

	SignerWithSetupMainUser() throws GoraException
	{
		userDataStore=DataStoreFactory.getDataStore(Long.class,User.class,new org.apache.hadoop.conf.Configuration());
	}

	void setupMainUser() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException,
		                            InvalidKeyException
	{
		final User user=new User();
		final ByteBuffer b=ByteBuffer.wrap("".getBytes());
		b.mark();
		user.setBlind(b);
		user.setId(clientId.getUser());
		final Map<CharSequence,Client> clients=new HashMap<>(1);
		final Client client=new Client();
		client.setComparisonIdentities(new ArrayList<>());
		client.setDeviceToken(deviceToken);
		final PublicKeys publicKeys=new PublicKeys();
		final com.kareebo.contacts.server.gora.EncryptionKey encryptionKey=new com.kareebo.contacts.server.gora
			                                                                       .EncryptionKey();
		encryptionKey.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
		encryptionKey.setBuffer(b);
		publicKeys.setEncryption(encryptionKey);
		publicKeys.setVerification(verificationKey);
		client.setKeys(publicKeys);
		final UserAgent userAgent=new UserAgent();
		userAgent.setPlatform("");
		userAgent.setVersion("");
		client.setUserAgent(userAgent);
		clients.put(TypeConverter.convert(clientId.getClient()),client);
		user.setClients(clients);
		user.setIdentities(new ArrayList<>());
		user.setSentRequests(new ArrayList<>());
		userDataStore.put(user.getId(),user);
	}
}
