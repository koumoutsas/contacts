package com.kareebo.contacts.test.integration.java;

import org.vertx.java.platform.Verticle;
import org.vertx.testtools.VertxAssert;

public class SomeVerticle extends Verticle {

  public void start() {
    VertxAssert.initialize(vertx);

    // You can also assert from other verticles!!
    VertxAssert.assertEquals("foo", "foo");

    // And complete tests from other verticles!!
    VertxAssert.testComplete();
  }
}
