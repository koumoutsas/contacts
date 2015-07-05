package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.ContactOperationSetPlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.HashedContact;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.ContactOperation;
import com.kareebo.contacts.thrift.InvalidArgument;
import com.kareebo.contacts.thrift.Signature;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

/**
 * Service implementation for updating the server-side contact book
 */
public class UpdateServerContactBook extends SignatureVerifier implements com.kareebo.contacts.thrift
	                                                                          .UpdateServerContactBook.AsyncIface
{
	private Set<ContactOperation> contactOperationSet;

	/**
	 * Constructor from a datastore
	 *
	 * @param dataStore The datastore
	 */
	UpdateServerContactBook(final DataStore<Long,User> dataStore)
	{
		super(dataStore);
	}

	@Override
	public void updateServerContactBook1(final Set<ContactOperation> contactOperationSet,final Signature signature,final Future<Void> future)
	{
		this.contactOperationSet=contactOperationSet;
		verify(new ContactOperationSetPlaintextSerializer(contactOperationSet),signature,future);
	}

	@Override
	void afterVerification(final Client client) throws InvalidArgument
	{
		final Map<CharSequence,HashedContact> contacts=client.getContacts();
		for(ContactOperation contactOperation:contactOperationSet)
		{
			final com.kareebo.contacts.common.HashedContact hashedContact=contactOperation.getContact();
			final CharSequence id=TypeConverter.convert(hashedContact.getId());
			HashedContact dbHashedContact=contacts.get(id);
			switch(contactOperation.getType())
			{
				case Add:
					try
					{
						if(dbHashedContact==null)
						{
							dbHashedContact=new HashedContact();
						}
						dbHashedContact.setHash(TypeConverter.convert(hashedContact.getHash()));
						contacts.put(id,dbHashedContact);
					}
					catch(NoSuchAlgorithmException e)
					{
						throw new InvalidArgument();
					}
					break;
				case Delete:
					if(dbHashedContact==null)
					{
						throw new InvalidArgument();
					}
					contacts.remove(id);
					break;
			}
		}
	}
}
