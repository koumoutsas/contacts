package com.kareebo.contacts;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class PingVerticle extends Verticle {

  public void start() {

    final Logger logger = container.logger();

    vertx.eventBus().registerHandler("ping-address", new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        message.reply("pong!");
        logger.info("Sent back pong");
      }
    });


    logger.info("PingVerticle started");

  }
}
