package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.ContactOperationSetPlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.EncryptedBuffer;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.ContactOperation;
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
import java.util.HashSet;
import java.util.Set;

/**
 * Service implementation for updating the server-side contact book
 */
public class UpdateServerContactBook extends SignatureVerifierWithIdentityStore implements com.kareebo.contacts.thrift
	                                                                                           .UpdateServerContactBook.AsyncIface
{
	private static final Logger logger=LoggerFactory.getLogger(UpdateServerContactBook.class.getName());
	final private GraphAccessor graphAccessor;

	/**
	 * Constructor from a datastore
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
	public void updateServerContactBook1(final Set<ContactOperation> contactOperationSet,final SignatureBuffer
		                                                                                     signature,final Future<Void> future)
	{
		verify(new ContactOperationSetPlaintextSerializer(contactOperationSet),signature,future,new After()
		{
			@Override
			public void run(final User user,final Client client) throws FailedOperation
			{
				final int maxSize=contactOperationSet.size();
				if(maxSize==0)
				{
					return;
				}
				final ArrayList<ContactOperation> addOperations=new ArrayList<>(maxSize);
				final ArrayList<ContactOperation> deleteOperations=new ArrayList<>(maxSize);
				final ArrayList<ContactOperation> updateOperations=new ArrayList<>(maxSize);
				for(final ContactOperation contactOperation : contactOperationSet)
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
							logger.error("Unknown operation "+contactOperation.getType()+" for "+client.toString());
							throw new FailedOperation();
					}
				}
				final HashSet<Long> addedContacts=new HashSet<>(maxSize);
				final HashSet<Long> deletedContacts=new HashSet<>(maxSize);
				final HashSet<EncryptedBuffer> comparisonIdentities=new HashSet<>(maxSize);
				for(final EncryptedBuffer e : client.getComparisonIdentities())
				{
					final EncryptedBuffer newE=new EncryptedBuffer();
					newE.setAlgorithm(e.getAlgorithm());
					final ByteBuffer b=e.getBuffer();
					b.rewind();
					b.mark();
					newE.setBuffer(b);
					comparisonIdentities.add(newE);
				}
				try
				{
					if(!addOperations.isEmpty())
					{
						for(final ContactOperation c : addOperations)
						{
							if(!comparisonIdentities.add(TypeConverter.convert(c.getComparisonIdentity())))
							{
								logger.error("Unable to add comparison identity for "+c.toString());
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
								logger.error("Unable to remove comparison identity for "+c.toString());
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
				put(client);
			}

			private void resolveAndAdd(final HashBuffer contact,final Set<Long> set) throws FailedOperation
			{
				final Long resolved=find(contact.bufferForBuffer());
				if(resolved==null)
				{
					logger.error("Unknown contact "+contact.toString());
					throw new FailedOperation();
				}
				set.add(resolved);
			}
		});
	}
}
