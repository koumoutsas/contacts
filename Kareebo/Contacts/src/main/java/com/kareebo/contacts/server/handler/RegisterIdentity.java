package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.HashBufferPlaintextSerializer;
import com.kareebo.contacts.base.RandomHashPad;
import com.kareebo.contacts.base.RegisterIdentityInputPlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for registering an identity
 */
public class RegisterIdentity extends SignatureVerifierWithIdentityStore implements com.kareebo.contacts.thrift.RegisterIdentity.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(RegisterIdentity.class.getName());

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore         The datastore
	 * @param identityDataStore The identity datastore
	 */
	RegisterIdentity(final DataStore<Long,User> dataStore,final DataStore<ByteBuffer,HashIdentity> identityDataStore)
	{
		super(dataStore,identityDataStore);
	}

	@Override
	public void registerIdentity1(final HashBuffer uA,final SignatureBuffer signature,final Future<RegisterIdentityReply> future)
	{
		final RegisterIdentityReply reply=new RegisterIdentityReply();
		verify(new HashBufferPlaintextSerializer(uA),signature,new Reply<>(future,reply),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				final ByteBuffer key=uA.bufferForBuffer();
				HashIdentityValue identity=get(key);
				Long id;
				User newUser;
				if(identity==null)
				{
					newUser=clientDBAccessor.createNewUser();
					id=newUser.getId();
					identity=new HashIdentityValue();
					identity.setId(id);
					identity.setConfirmers(new ArrayList<Long>(1));
				}
				else
				{
					id=identity.getId();
					newUser=clientDBAccessor.get(id);
				}
				final ByteBuffer blind=new RandomHashPad().getBytes();
				newUser.setBlind(blind);
				final List<Long> confirmers=identity.getConfirmers();
				confirmers.add(user.getId());
				identity.setConfirmers(confirmers);
				put(key,identity);
				clientDBAccessor.put(newUser);
				reply.setId(id);
				reply.setBlind(blind);
			}
		});
	}

	@Override
	public void registerIdentity2(final long userIdA,final Future<RegisterIdentityReply> future)
	{
		try
		{
			final ByteBuffer blind=clientDBAccessor.get(userIdA).getBlind();
			blind.rewind();
			blind.mark();
			final Long id=clientDBAccessor.createNewClient();
			clientDBAccessor.close();
			future.setResult(new RegisterIdentityReply(id,blind));
		}
		catch(FailedOperation failedOperation)
		{
			logger.error("Unable to find user for "+userIdA);
			future.setFailure(failedOperation);
		}
	}

	@Override
	public void registerIdentity3(final RegisterIdentityInput registerIdentityInput,final SignatureBuffer signature,final Future<Void> future)
	{
		final ByteBuffer uA=registerIdentityInput.getUA().bufferForBuffer();
		final HashIdentityValue identityValue=get(uA);
		final Long clientId=identityValue.getId();
		if(registerIdentityInput.getUserIdA()!=clientId)
		{
			logger.error("Non-matching user ids. Expected "+clientId+", but got "+registerIdentityInput.getUserIdA());
			future.setFailure(new FailedOperation());
			return;
		}
		try
		{
			final Client client=clientDBAccessor.get(signature.getClient());
			client.setKeys(TypeConverter.convert(registerIdentityInput.getPublicKeys()));
			client.setUserAgent(TypeConverter.convert(registerIdentityInput.getUserAgent()));
			clientDBAccessor.put(client);
		}
		catch(FailedOperation failedOperation)
		{
			future.setFailure(failedOperation);
			return;
		}
		catch(NoSuchAlgorithmException e)
		{
			logger.error("Error converting algorithm: "+e.toString());
			future.setFailure(new FailedOperation());
			return;
		}
		verify(new RegisterIdentityInputPlaintextSerializer(registerIdentityInput),signature,new Reply(future),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				final List<com.kareebo.contacts.server.gora.HashBuffer> identities=user.getIdentities();
				final HashBuffer uJ=registerIdentityInput.getUJ();
				boolean foundUJ=false;
				for(final HashBuffer h : registerIdentityInput.getUSet())
				{
					final HashIdentityValue value=new HashIdentityValue();
					value.setId(clientId);
					final List<Long> confirmers=new ArrayList<>();
					if(h.equals(uJ))
					{
						foundUJ=true;
						aliasTo(uA,h.bufferForBuffer());
						confirmers.addAll(identityValue.getConfirmers());
					}
					value.setConfirmers(confirmers);
					put(h.bufferForBuffer(),value);
					try
					{
						identities.add(TypeConverter.convert(h));
					}
					catch(NoSuchAlgorithmException e)
					{
						logger.error("Error converting algorithm: "+e.toString());
						throw new FailedOperation();
					}
				}
				if(!foundUJ)
				{
					logger.error("Wrong input. Unable to find the primary identity in the identity set");
					throw new FailedOperation();
				}
				clientDBAccessor.put(user);
			}
		});
	}
}
