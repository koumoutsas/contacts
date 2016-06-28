package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.RandomHashPad;
import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for registering an identity
 */
class RegisterIdentity extends SignatureVerifierWithIdentityStore implements com.kareebo.contacts.thrift.RegisterIdentity.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(RegisterIdentity.class.getName());

	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The user datastore
	 * @param identityDataStore The identity datastore
	 */
	RegisterIdentity(final @Nonnull DataStore<Long,User> userDataStore,final @Nonnull DataStore<ByteBuffer,HashIdentity> identityDataStore)
	{
		super(userDataStore,identityDataStore);
	}

	@Override
	public void registerIdentity1(final @Nonnull HashBuffer uA,final @Nonnull SignatureBuffer signature,final @Nonnull Future<RegisterIdentityReply> future)
	{
		final RegisterIdentityReply reply=new RegisterIdentityReply();
		verify(uA,signature,new Reply<>(future,reply),(user,client)->{
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
				identity.setConfirmers(new ArrayList<>(1));
			}
			else
			{
				id=identity.getId();
				newUser=clientDBAccessor.get(id);
			}
			final ByteBuffer blind=new RandomHashPad().getByteBuffer();
			newUser.setBlind(blind);
			final List<Long> confirmers=identity.getConfirmers();
			confirmers.add(user.getId());
			identity.setConfirmers(confirmers);
			put(key,identity);
			clientDBAccessor.put(newUser);
			reply.setId(id);
			reply.setBlind(blind);
		});
	}

	@Override
	public void registerIdentity2(final long userIdA,final @Nonnull Future<RegisterIdentityReply> future)
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
	public void registerIdentity3(final @Nonnull RegisterIdentityInput registerIdentityInput,final @Nonnull SignatureBuffer signature,final @Nonnull Future<Void> future)
	{
		final ByteBuffer uA=registerIdentityInput.getUA().bufferForBuffer();
		try
		{
			final HashIdentityValue identityValue=get(uA);
			final Long clientId=identityValue.getId();
			if(registerIdentityInput.getUserIdA()!=clientId)
			{
				logger.error("Non-matching user ids. Expected "+clientId+", but got "+registerIdentityInput.getUserIdA());
				future.setFailure(new FailedOperation());
				return;
			}
			final Client client=clientDBAccessor.get(signature.getClient());
			client.setKeys(TypeConverter.convert(registerIdentityInput.getPublicKeys()));
			client.setUserAgent(TypeConverter.convert(registerIdentityInput.getUserAgent()));
			client.setDeviceToken(registerIdentityInput.getDeviceToken());
			clientDBAccessor.put(client);
			verify(registerIdentityInput,signature,new Reply<>(future),(user,client1)->{
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
						logger.error("Error converting algorithm",e);
						throw new FailedOperation();
					}
				}
				if(!foundUJ)
				{
					logger.error("Wrong input. Unable to find the primary identity in the identity set");
					throw new FailedOperation();
				}
				clientDBAccessor.put(user);
			});
		}
		catch(FailedOperation failedOperation)
		{
			future.setFailure(failedOperation);
		}
		catch(NoSuchAlgorithmException e)
		{
			logger.error("Error converting algorithm",e);
			future.setFailure(new FailedOperation());
		}
	}
}
