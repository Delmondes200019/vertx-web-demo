package com.vertx.web.demo.vertx_stock_broker;

import com.vertx.web.demo.vertx_stock_broker.config.ConfigLoader;
import com.vertx.web.demo.vertx_stock_broker.config.VersionInfoVerticle;
import com.vertx.web.demo.vertx_stock_broker.config.migration.FlywayMigration;
import com.vertx.web.demo.vertx_stock_broker.restapi.RestApiVerticle;
import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

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
    vertx.deployVerticle(VersionInfoVerticle.class.getName())
      .onFailure(startPromise::fail)
      .onSuccess(deploymentId -> LOG.info("Deployed ".concat(VersionInfoVerticle.class.getName()).concat("with id "
        .concat(deploymentId))))
      .compose(s -> migrateDatabase())
      .onFailure(startPromise::fail)
      .onSuccess(deploymentID -> LOG.info("Migrated schema to the latest version!"))
      .compose(next -> deployRestApiVerticle(startPromise));
  }

  private Future<Void> migrateDatabase() {
    return ConfigLoader.load(vertx)
      .compose(config -> FlywayMigration.migrate(vertx, config.getDbConfig()));
  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName(), new DeploymentOptions()
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
