package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.common.PublicKeys;
import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.Client;
import com.kareebo.contacts.thrift.InvalidArgument;
import com.kareebo.contacts.thrift.ModifyKeys;
import com.kareebo.contacts.thrift.Signature;
import org.vertx.java.core.Future;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Vector;

/**
 * Key modification operation
 */
public class ModifyKeysAsyncIface extends ClientDBAccessor implements ModifyKeys.AsyncIface
{
	@Override
	/**
	 * The client sends the new keys signed with the old keys
	 */
	public void modifyKeys1(final PublicKeys newPublicKeys,final Signature signature,final Future<Void> future)
	{
		final Client client;
		try
		{
			client=get(signature.getIds());
		}
		catch(InvalidArgument invalidArgument)
		{
			future.setFailure(invalidArgument);
			return;
		}
		final Vector<byte[]> plaintext=new Vector<>(2);
		plaintext.add(newPublicKeys.getEncryption().getBuffer());
		plaintext.add(newPublicKeys.getVerification().getBuffer());
		try
		{
			if(!Utils.verifySignature(client.getKeys().getVerification(),signature.getSignature(),plaintext))
			{
				future.setFailure(new InvalidArgument());
				return;
			}
			client.setKeys(TypeConverter.convert(newPublicKeys));
		}
		catch(NoSuchProviderException|NoSuchAlgorithmException|SignatureException|InvalidKeyException|InvalidKeySpecException e)
		{
			future.setFailure(new InvalidArgument());
			return;
		}
		set(client);
		close();
	}
}
