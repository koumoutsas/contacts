package com.kareebo.contacts.client.externalServices;

import com.kareebo.contacts.thrift.HashAlgorithm;
import com.kareebo.contacts.thrift.HashBuffer;
import com.kareebo.contacts.thrift.client.ClientConstants;
import com.kareebo.contacts.thrift.client.ContactType;
import com.kareebo.contacts.thrift.client.Identity;
import org.apache.thrift.TSerializer;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link InvitationRequestLink}
 */
public class InvitationRequestLinkTest
{
	static
	{
		try
		{
			// The reason we use a ProtocolTest.TestConnector is to be able to run multiple tests in parallel with the same behavior,
			// without being restricted by URL's setURLStreamHandlerFactory restriction that it's called only once per JVM
			Protocol.registerProtocol(new ProtocolTest.TestConnector());
		}
		catch(Protocol.AlreadyRegistered ignored)
		{
		}
	}

	@Test
	public void generate() throws Exception
	{
		final Identity identity=new Identity("b",ContactType.Email,new HashBuffer(null,HashAlgorithm.SHA256));
		final com.kareebo.contacts.thrift.client.InvitationRequestLink thrift=new com.kareebo.contacts.thrift.client.InvitationRequestLink("a",ContactType.Address,identity);
		assertEquals(new URL(ClientConstants.Protocol,"invitation",new TSerializer().toString(thrift)),new InvitationRequestLink(thrift).generate());
	}
}