package com.kareebo.contacts.integrationTest.serverClient;

import com.kareebo.contacts.base.TypeConverter;
import com.kareebo.contacts.client.protocol.ModifyKeys;
import com.kareebo.contacts.client.protocol.ServiceMethod;
import com.kareebo.contacts.thrift.*;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * Integration test for the server-client protocol for the ModifyKeys service
 */
public class ModifyKeysAgentIntegrationTest extends SetterIntegrationTest<PublicKeys>
{
	@Nonnull
	@Override
	protected ThrowingSupplier<PublicKeys> stateRetrieverImplementation()
	{
		return ()->TypeConverter.convert(getClient().getKeys());
	}

	@Nonnull
	@Override
	protected PublicKeys payload()
	{
		final ByteBuffer byteBuffer=ByteBuffer.wrap("a".getBytes());
		byteBuffer.mark();
		return new PublicKeys(new EncryptionKey(byteBuffer,EncryptionAlgorithm.RSA2048),new VerificationKey(byteBuffer,SignatureAlgorithm.SHA512withECDSAprime239v1));
	}

	@Nonnull
	@Override
	ServiceMethod serviceMethod()
	{
		return ModifyKeys.method1;
	}

	@Override
	protected boolean isIdempotent()
	{
		return false;
	}
}
