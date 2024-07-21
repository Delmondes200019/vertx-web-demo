package com.vertx.web.demo.vertx_stock_broker;

import com.vertx.web.demo.vertx_stock_broker.restapi.RestApiVerticle;
import com.vertx.web.demo.vertx_stock_broker.restapi.assets.AssetsRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.QuotesRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 8888;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(throwable -> LOG.error("Unhandled: {}", throwable.getCause()));
    vertx.deployVerticle(new MainVerticle())
      .onFailure(throwable -> LOG.error("Failed to deploy ", throwable))
      .onSuccess(deploymentId -> LOG.info("Deployed ".concat(MainVerticle.class.getName())
        .concat("with id ".concat(deploymentId)))
      );
  }

  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(RestApiVerticle.class.getName(), new DeploymentOptions()
        .setInstances(getAvailableProcessors()))
      .onFailure(startPromise::fail)
      .onSuccess(deploymentId -> {
        LOG.info("Deployed ".concat(RestApiVerticle.class.getName())
          .concat("with id ".concat(deploymentId)));
        startPromise.complete();
      });
  }

  private static int getAvailableProcessors() {
    return Math.max(1, Runtime.getRuntime().availableProcessors());
  }
}
