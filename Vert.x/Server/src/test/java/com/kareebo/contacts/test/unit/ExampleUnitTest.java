package com.kareebo.contacts.test.unit;

import com.kareebo.contacts.PingVerticle;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {

  @Test
  public void testVerticle() {
    PingVerticle vert = new PingVerticle();

    // Interrogate your classes directly....

    assertNotNull(vert);
  }
}
