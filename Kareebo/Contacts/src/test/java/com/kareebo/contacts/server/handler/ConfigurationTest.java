package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.PendingNotification;
import com.kareebo.contacts.server.gora.User;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link Configuration}
 */
public class ConfigurationTest
{
	@Test
	public void get() throws Exception
	{
		final org.apache.hadoop.conf.Configuration datastoreConfiguration=new org.apache.hadoop.conf.Configuration();
		final DataStore<Long,User> userDatastore=DataStoreFactory.getDataStore(Long.class,User.class,datastoreConfiguration);
		final DataStore<ByteBuffer,HashIdentity> identityDatastore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,datastoreConfiguration);
		final ClientNotifier clientNotifier=new ClientNotifier((deviceToken,payload)->{
		},DataStoreFactory.getDataStore(Long.class,PendingNotification.class,datastoreConfiguration));
		final GraphAccessor graphAccessor=new GraphAccessor()
		{
			@Override
			public void addEdges(final Long from,@Nonnull final HashSet<Long> to)
			{
			}

			@Override
			public void removeEdges(@Nonnull final Long from,@Nonnull final HashSet<Long> to) throws IllegalStateException
			{
			}

			@Override
			public void close()
			{
			}
		};
		final Configuration configuration=new Configuration(userDatastore,identityDatastore,clientNotifier,graphAccessor);
		assertEquals(userDatastore,configuration.getUserDataStore());
		assertEquals(identityDatastore,configuration.getIdentityDatastore());
		assertEquals(clientNotifier,configuration.getClientNotifier());
		assertEquals(graphAccessor,configuration.getGraphAccessor());
	}
}