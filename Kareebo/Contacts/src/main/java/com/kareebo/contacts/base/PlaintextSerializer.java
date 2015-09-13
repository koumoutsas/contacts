package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.FailedOperation;

/**
 * Interface for serializing an object into an array of byte arrays for sending for signature
 */
public interface PlaintextSerializer
{
	byte[] serialize() throws FailedOperation;
}
