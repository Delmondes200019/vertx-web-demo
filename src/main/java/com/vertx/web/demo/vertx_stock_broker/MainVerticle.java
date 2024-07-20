package com.vertx.web.demo.vertx_stock_broker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  private static Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(throwable -> LOG.error("Unhandled: {}", throwable.getCause()));
    vertx.deployVerticle(new MainVerticle(), stringAsyncResult -> {
      if(stringAsyncResult.failed()){
        LOG.error("Failed to deploy: {}", stringAsyncResult.cause());
        return;
      }
      LOG.info("Deployed ".concat(MainVerticle.class.getName()));
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888).onComplete(http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
