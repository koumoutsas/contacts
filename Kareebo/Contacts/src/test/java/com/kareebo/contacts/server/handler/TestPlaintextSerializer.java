package com.kareebo.contacts.server.handler;

/**
 * Wrapper around an existing plaintext for tests
 */
public class TestPlaintextSerializer implements com.kareebo.contacts.base.PlaintextSerializer
{
	private byte[] plaintext;

	public TestPlaintextSerializer(final byte[] plaintext)
	{
		this.plaintext=plaintext;
	}

	@Override
	public byte[] serialize()
	{
		return plaintext;
	}
}

