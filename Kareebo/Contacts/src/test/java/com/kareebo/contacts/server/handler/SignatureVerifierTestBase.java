package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.EncryptionAlgorithm;
import com.kareebo.contacts.thrift.EncryptionKey;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

/**
 * Base class for all test suites for {@link SignatureVerifier} subclasses
 */
abstract class SignatureVerifierTestBase extends Signer
{
	final ClientId clientIdValid=new ClientId();
	final PublicKeys publicKeys=new PublicKeys();
	SignatureBuffer signature;
	SignatureBuffer wrongSignature;
	SignatureVerifier signatureVerifier;
	Client clientValid;
	UserAgent userAgent;
	long userId;
	DataStore<Long,User> dataStore;

	public void setUp() throws Exception
	{
		dataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		final User userValid=new User();
		final byte[] buffer={'a','b'};
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		userValid.setBlind(byteBuffer);
		final HashMap<CharSequence,Client> clients=new HashMap<>();
		userValid.setClients(clients);
		userValid.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		userValid.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
		dataStore.put(userId,userValid);
		setUpCrypto();
		clientIdValid.setClient(0);
		clientIdValid.setUser(userValid.getId());
		userAgent=new UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		publicKeys.setEncryption(TypeConverter.convert(setUpEncryptionKey(buffer)));
		publicKeys.setVerification(verificationKey);
		clientValid=new Client();
		clientValid.setUserAgent(userAgent);
		clientValid.setKeys(publicKeys);
		clientValid.setComparisonIdentities(new ArrayList<EncryptedBuffer>());
		signatureVerifier=construct(dataStore);
		signatureVerifier.clientDBAccessor.put(clientIdValid,clientValid);
		assertNotNull(signatureVerifier);
	}

	private void setUpCrypto() throws NoSuchProviderException, NoSuchAlgorithmException,
		                                  InvalidAlgorithmParameterException, InvalidKeyException, SignatureException, InvalidKeySpecException, TException
	{
		signature=sign(constructPlaintext(),clientIdValid);
		wrongSignature=sign("feg".getBytes(),clientIdValid);
	}

	static EncryptionKey setUpEncryptionKey(final byte[] buffer)
	{
		final EncryptionKey encryptionKey=new EncryptionKey();
		encryptionKey.setAlgorithm(EncryptionAlgorithm.RSA2048);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		encryptionKey.setBuffer(byteBuffer);
		return encryptionKey;
	}

	abstract SignatureVerifier construct(final DataStore<Long,User> dataStore);

	abstract TBase constructPlaintext();

	long addUsers(final int n)
	{
		final long ret=userId+1;
		for(int i=0;i<n;++i)
		{
			final User user=new User();
			final ByteBuffer byteBuffer=ByteBuffer.wrap("".getBytes());
			byteBuffer.mark();
			user.setBlind(byteBuffer);
			user.setClients(new HashMap<CharSequence,Client>());
			user.setIdentities(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
			user.setSentRequests(new ArrayList<com.kareebo.contacts.server.gora.HashBuffer>());
			dataStore.put(++userId,user);
			dataStore.close();
		}
		return ret;
	}

	User getUserValid()
	{
		return dataStore.get(clientIdValid.getUser());
	}
}
