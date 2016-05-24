package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.EncryptedBuffer;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for updating the server-side contact book
 */
public class UpdateServerContactBook extends SignatureVerifierWithIdentityStore implements com.kareebo.contacts.thrift
	                                                                                           .UpdateServerContactBook.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(UpdateServerContactBook.class.getName());
	final private GraphAccessor graphAccessor;

	/**
	 * Constructor from datastores
	 *
	 * @param userDataStore     The datastore of the users
	 * @param identityDatastore The datastore of hashed identities
	 */
	public UpdateServerContactBook(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity>
		                                                                        identityDatastore,final GraphAccessor graphAccessor)
	{
		super(userDataStore,identityDatastore);
		this.graphAccessor=graphAccessor;
	}

	@Override
	public void updateServerContactBook1(final ContactOperationSet contactOperationSet,final SignatureBuffer
		                                                                                   signature,final Future<Void> future)
	{
		verify(contactOperationSet,signature,new Reply<>(future),new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				final Set<ContactOperation> contactOperations=contactOperationSet.getContactOperations();
				final int maxSize=contactOperations.size();
				if(maxSize==0)
				{
					return;
				}
				final ArrayList<ContactOperation> addOperations=new ArrayList<>(maxSize);
				final ArrayList<ContactOperation> deleteOperations=new ArrayList<>(maxSize);
				final ArrayList<ContactOperation> updateOperations=new ArrayList<>(maxSize);
				for(final ContactOperation contactOperation : contactOperations)
				{
					switch(contactOperation.getType())
					{
						case Add:
							addOperations.add(contactOperation);
							break;
						case Delete:
							deleteOperations.add(contactOperation);
							break;
						case Update:
							updateOperations.add(contactOperation);
							break;
						default:
							logger.error("Unknown operation "+contactOperation.getType()+" for "+client);
							throw new FailedOperation();
					}
				}
				final HashSet<Long> addedContacts=new HashSet<>(maxSize);
				final HashSet<Long> deletedContacts=new HashSet<>(maxSize);
				final Set<EncryptedBuffer> comparisonIdentities=client.getComparisonIdentities().stream().map(e->{
					final EncryptedBuffer newE=new EncryptedBuffer();
					newE.setAlgorithm(e.getAlgorithm());
					final ByteBuffer b=e.getBuffer();
					b.rewind();
					b.mark();
					newE.setBuffer(b);
					return newE;
				}).collect(Collectors
					           .toSet());
				try
				{
					if(!addOperations.isEmpty())
					{
						for(final ContactOperation c : addOperations)
						{
							if(!comparisonIdentities.add(TypeConverter.convert(c.getComparisonIdentity())))
							{
								logger.error("Unable to add comparison identity for "+c);
								throw new FailedOperation();
							}
							resolveAndAdd(c.getContact(),addedContacts);
						}
					}
					if(!deleteOperations.isEmpty())
					{
						for(final ContactOperation c : deleteOperations)
						{
							final EncryptedBuffer converted=TypeConverter.convert(c.getComparisonIdentity
								                                                        ());
							if(!comparisonIdentities.remove(converted))
							{
								logger.error("Unable to remove comparison identity for "+c);
								throw new FailedOperation();
							}
							resolveAndAdd(c.getContact(),deletedContacts);
						}
					}
					if(!updateOperations.isEmpty())
					{
						for(final ContactOperation c : updateOperations)
						{
							resolveAndAdd(c.getContact(),addedContacts);
						}
					}
					graphAccessor.addEdges(user.getId(),addedContacts);
					graphAccessor.removeEdges(user.getId(),deletedContacts);
					graphAccessor.close();
				}
				catch(NoSuchAlgorithmException|IllegalStateException e)
				{
					logger.error("Verification failure with exception",e);
					throw new FailedOperation();
				}
				client.setComparisonIdentities(new ArrayList<>(comparisonIdentities));
				clientDBAccessor.put(client);
			}

			private void resolveAndAdd(final HashBuffer contact,final Set<Long> set) throws FailedOperation
			{
				set.add(find(contact.bufferForBuffer()));
			}
		});
	}
}
