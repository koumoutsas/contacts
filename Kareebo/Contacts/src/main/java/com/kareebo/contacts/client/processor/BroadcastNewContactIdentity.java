package com.kareebo.contacts.client.processor;

import com.kareebo.contacts.base.RandomHashPad;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.client.persistentStorage.ContextRetriever;
import com.kareebo.contacts.client.persistentStorage.PersistedObjectRetriever;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.EncryptionKey;
import com.kareebo.contacts.thrift.MapClientIdEncryptionKey;
import com.kareebo.contacts.thrift.client.Identity;
import com.kareebo.contacts.thrift.client.PrivateKeys;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.persistentStorage.PersistentStorageConstants;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Client-processor-side implementation of the broadcast new contact identity service
 */
public class BroadcastNewContactIdentity extends Service
{
	private static final String serviceName=BroadcastNewContactIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");
	public final static ServiceMethod method4=new ServiceMethod(serviceName,"4");
	private final static ServiceMethod trigger=new ServiceMethod(serviceName,"0");
	private static final ServiceMethod[] methods={trigger,method1,method2,method3,method4};

	BroadcastNewContactIdentity(final @Nonnull Context context,final @Nonnull PersistedObjectRetriever persistedObjectRetriever)
	{
		super(context,persistedObjectRetriever);
	}

	@Nonnull
	@Override
	protected com.kareebo.contacts.thrift.client.jobs.ServiceMethod[] methodNames()
	{
		return methods;
	}

	@Nonnull
	@Override
	protected Functor[] functors()
	{
		return new Functor[]
			       {
				       new Functor<MapClientIdEncryptionKey>()
				       {
					       @Override
					       protected void runInternal(@Nonnull final PersistedObjectRetriever persistedObjectRetriever,@Nonnull final MapClientIdEncryptionKey payload,@Nonnull final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
					       {
						       final PrivateKeys privateKeys=new PrivateKeys();
						       persistedObjectRetriever.get(privateKeys,PersistentStorageConstants.PrivateKeys);
						       //noinspection unused
						       final RandomHashPad r=new RandomHashPad();
						       final ContextRetriever contextRetriever=new ContextRetriever(persistedObjectRetriever);
						       final Identity identityC=new Identity();
						       contextRetriever.get(identityC,context);
						       //noinspection StatementWithEmptyBody,unused
						       for(final Map.Entry<ClientId,EncryptionKey> entry : payload.getKeyMap().entrySet())
						       {
						       }
					       }
				       }
			       };
	}
}
