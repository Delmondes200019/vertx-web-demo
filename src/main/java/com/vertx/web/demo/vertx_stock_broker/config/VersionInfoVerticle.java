package com.vertx.web.demo.vertx_stock_broker.config;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class VersionInfoVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(VersionInfoVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        LOG.info("Application version: ".concat(configuration.getVersion()));
        startPromise.complete();
      });
  }
}
