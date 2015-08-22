package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.base.SetHashBufferPlaintextSerializer;
import com.kareebo.contacts.base.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Service implementation for registering an unconfirmed identity
 */
public class RegisterUnconfirmedIdentity extends SignatureVerifier implements com.kareebo.contacts.thrift.RegisterUnconfirmedIdentity.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(RegisterUnconfirmedIdentity.class.getName());
	final private HashIdentityRetriever hashIdentityRetriever;
	/**
	 * Stores the {@link HashBuffer} set
	 */
	private Set<HashBuffer> uSet;
	/**
	 * The datastore of hashed identities
	 */
	private DataStore<ByteBuffer,HashIdentity>
		identityDatastore;

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore         The datastore
	 * @param identityDatastore The datastore of hashed identities
	 */
	public RegisterUnconfirmedIdentity(final DataStore<Long,User> dataStore,final DataStore<ByteBuffer,HashIdentity>
		                                                                        identityDatastore)
	{
		super(dataStore);
		this.identityDatastore=identityDatastore;
		hashIdentityRetriever=new HashIdentityRetriever(identityDatastore);
	}

	@Override
	public void registerUnconfirmedIdentity1(final Set<HashBuffer> uSet,final SignatureBuffer signature,final Future<Void> future)
	{
		this.uSet=uSet;
		verify(new SetHashBufferPlaintextSerializer(uSet),signature,future);
	}

	@Override
	void verify(final PlaintextSerializer plaintextSerializer,final SignatureBuffer signature,final Future<Void> future)
	{
		super.verify(plaintextSerializer,signature,future);
		if(future.succeeded())
		{
			identityDatastore.close();
		}
	}

	@Override
	void afterVerification(final User user,final Client client) throws FailedOperation
	{
		final Set<com.kareebo.contacts.server.gora.HashBuffer> identitySet=Utils.convertToSet(user.getIdentities());
		for(final HashBuffer h : uSet)
		{
			try
			{
				final com.kareebo.contacts.server.gora.HashBuffer dbH=TypeConverter.convert(h);
				if(!identitySet.add(dbH))
				{
					logger.error("Unable to add hashed identity for "+h.toString());
					throw new FailedOperation();
				}
				final ByteBuffer b=dbH.getBuffer();
				if(hashIdentityRetriever.find(b)!=null)
				{
					logger.error("Hashed identity for "+h.toString()+" already exists in identity datastore");
					throw new FailedOperation();
				}
				final HashIdentity hashIdentity=new HashIdentity();
				hashIdentity.setHash(b);
				final HashIdentityValue hashIdentityValue=new HashIdentityValue();
				hashIdentityValue.setConfirmers(new ArrayList<Long>());
				hashIdentityValue.setId(user.getId());
				hashIdentity.setHashIdentity(hashIdentityValue);
				identityDatastore.put(b,hashIdentity);
			}
			catch(NoSuchAlgorithmException e)
			{
				logger.error("Verification failure with exception",e);
				throw new FailedOperation();
			}
		}
		user.setIdentities(new ArrayList<>(identitySet));
	}
}
