package com.kareebo.contacts.client.service;

/**
 * Implementation of {@link MockResult} for {@link Void}
 */
public class MockVoidResult extends MockResult<Void>
{
	/**
	 * @param success Whether the result was successful
	 */
	MockVoidResult(final boolean success)
	{
		super(success);
	}

	@Override
	public Void result()
	{
		return null;
	}
}
