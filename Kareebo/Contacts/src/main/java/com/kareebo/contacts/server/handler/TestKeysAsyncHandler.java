package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.thrift.IdPair;
import com.kareebo.contacts.thrift.Signature;
import com.kareebo.contacts.thrift.TestKeys.AsyncIface;
import org.vertx.java.core.Future;

import java.nio.ByteBuffer;

/**
 * Async interface for the test keys service
 */
public class TestKeysAsyncHandler implements AsyncIface
{

    @Override
    public void testKeys1(String fixed, IdPair ids, Future<ByteBuffer> future) {

    }

    @Override
    public void testKeys2(String fixed, Signature signature, Future<Boolean> future) {

    }
}
