package com.kareebo.contacts.server.handler;

import java.util.Vector;

/**
 * Wrapper around an existing plaintext for tests
 */
public class TestPlaintextSerializer implements com.kareebo.contacts.base.PlaintextSerializer
{
	private Vector<byte[]> plaintext;

	public TestPlaintextSerializer(final Vector<byte[]> plaintext)
	{
		this.plaintext=plaintext;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		return plaintext;
	}
}

