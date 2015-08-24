package com.kareebo.contacts.server.handler;

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
public class RegisterUnconfirmedIdentity extends SignatureVerifierWithIdentityStore implements com.kareebo.contacts.thrift.RegisterUnconfirmedIdentity.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(RegisterUnconfirmedIdentity.class.getName());

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore         The datastore
	 * @param identityDatastore The datastore of hashed identities
	 */
	public RegisterUnconfirmedIdentity(final DataStore<Long,User> dataStore,final DataStore<ByteBuffer,HashIdentity>
		                                                                        identityDatastore)
	{
		super(dataStore,identityDatastore);
	}

	@Override
	public void registerUnconfirmedIdentity1(final Set<HashBuffer> uSet,final SignatureBuffer signature,final Future<Void> future)
	{
		verify(new SetHashBufferPlaintextSerializer(uSet),signature,future,new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
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
						if(find(b)!=null)
						{
							logger.error("Hashed identity for "+h.toString()+" already exists in identity datastore");
							throw new FailedOperation();
						}
						final HashIdentityValue hashIdentity=new HashIdentityValue();
						hashIdentity.setConfirmers(new ArrayList<Long>());
						hashIdentity.setId(user.getId());
						put(b,hashIdentity);
					}
					catch(NoSuchAlgorithmException e)
					{
						logger.error("Verification failure with exception",e);
						throw new FailedOperation();
					}
				}
				user.setIdentities(new ArrayList<>(identitySet));
				put(user);
			}
		});
	}
}
