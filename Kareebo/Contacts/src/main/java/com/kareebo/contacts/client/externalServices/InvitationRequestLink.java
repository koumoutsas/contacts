package com.kareebo.contacts.client.externalServices;

import com.kareebo.contacts.thrift.client.ClientConstants;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Generate a URL from an {@link com.kareebo.contacts.thrift.client.InvitationRequestLink}
 */
class InvitationRequestLink
{
	private final static String host="invitation";
	final private com.kareebo.contacts.thrift.client.InvitationRequestLink invitationRequestLink;

	InvitationRequestLink(final com.kareebo.contacts.thrift.client.InvitationRequestLink invitationRequestLink)
	{
		this.invitationRequestLink=invitationRequestLink;
	}

	/**
	 * Generate a {@link URL} for the {@link InvitationRequestLink}
	 *
	 * @return A {@link URL} with protocol {@link ClientConstants#Protocol} so that it is handled by the application
	 * @throws TException
	 * @throws MalformedURLException
	 */
	@Nonnull
	URL generate() throws TException, MalformedURLException
	{
		return new URL(ClientConstants.Protocol,host,new TSerializer().toString(invitationRequestLink));
	}
}
