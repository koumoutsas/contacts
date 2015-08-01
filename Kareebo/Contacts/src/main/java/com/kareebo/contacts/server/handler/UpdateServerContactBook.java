package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.ContactOperationSetPlaintextSerializer;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.ContactOperation;
import com.kareebo.contacts.thrift.FailedOperation;
import com.kareebo.contacts.thrift.SignatureBuffer;
import org.apache.gora.store.DataStore;
import org.vertx.java.core.Future;

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
	public void updateServerContactBook1(final Set<ContactOperation> contactOperationSet,final SignatureBuffer
		                                                                                     signature,final Future<Void> future)
	{
		this.contactOperationSet=contactOperationSet;
		verify(new ContactOperationSetPlaintextSerializer(contactOperationSet),signature,future);
	}

	@Override
	void afterVerification(final User user,final Client client) throws FailedOperation
	{
		for(ContactOperation contactOperation:contactOperationSet)
		{
			switch(contactOperation.getType())
			{
				case Add:
					break;
				case Delete:
					break;
				case Update:
					break;
				default:
					throw new FailedOperation();
			}
		}
	}
}
