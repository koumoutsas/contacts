package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.FailedOperation;

import java.util.Vector;

/**
 * Interface for serializing an object into an array of byte arrays for sending for signature
 */
public interface PlaintextSerializer
{
	Vector<byte[]> serialize() throws FailedOperation;
}
