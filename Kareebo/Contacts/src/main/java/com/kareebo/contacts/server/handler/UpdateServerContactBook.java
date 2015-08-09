package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.ContactOperationSetPlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.EncryptedBuffer;
import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.ContactOperation;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Service implementation for updating the server-side contact book
 */
public class UpdateServerContactBook extends SignatureVerifier implements com.kareebo.contacts.thrift
	                                                                          .UpdateServerContactBook.AsyncIface
{
	final private HashIdentityRetriever hashIdentityRetriever;
	final private GraphAccessor graphAccessor;
	private Set<ContactOperation> contactOperationSet;

	/**
	 * Constructor from a datastore
	 *
	 * @param userDataStore     The datastore of the users
	 * @param identityDatastore The datastore of hashed identities
	 */
	UpdateServerContactBook(final DataStore<Long,User> userDataStore,final DataStore<ByteBuffer,HashIdentity>
		                                                                 identityDatastore,final GraphAccessor graphAccessor)
	{
		super(userDataStore);
		hashIdentityRetriever=new HashIdentityRetriever(identityDatastore);
		this.graphAccessor=graphAccessor;
	}

	@Override
	public void updateServerContactBook1(final Set<ContactOperation> contactOperationSet,final SignatureBuffer
		                                                                                     signature,final Future<Void> future)
	{
		this.contactOperationSet=contactOperationSet;
		verify(new ContactOperationSetPlaintextSerializer(contactOperationSet),signature,future);
	}

	@Override
	void afterVerification(final User user,final Client client) throws FailedOperation
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
						throw new FailedOperation();
					}
					final Long resolved=hashIdentityRetriever.find(c.getContact().bufferForBuffer());
					if(resolved==null)
					{
						throw new FailedOperation();
					}
					addedContacts.add(resolved);
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
						throw new FailedOperation();
					}
					final Long resolved=hashIdentityRetriever.find(c.getContact().bufferForBuffer());
					if(resolved==null)
					{
						throw new FailedOperation();
					}
					deletedContacts.add(hashIdentityRetriever.find(c.getContact().bufferForBuffer()));
				}
			}
			if(!updateOperations.isEmpty())
			{
				for(final ContactOperation c : updateOperations)
				{
					final Long resolved=hashIdentityRetriever.find(c.getContact().bufferForBuffer());
					if(resolved==null)
					{
						throw new FailedOperation();
					}
					addedContacts.add(resolved);
				}
			}
			graphAccessor.addEdges(user.getId(),addedContacts);
			graphAccessor.removeEdges(user.getId(),deletedContacts);
			graphAccessor.close();
		}
		catch(NoSuchAlgorithmException|IllegalStateException e)
		{
			throw new FailedOperation();
		}
		client.setComparisonIdentities(new ArrayList<>(comparisonIdentities));
	}
}
