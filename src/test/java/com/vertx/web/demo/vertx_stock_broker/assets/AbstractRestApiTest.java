package com.vertx.web.demo.vertx_stock_broker.assets;

import com.vertx.web.demo.vertx_stock_broker.MainVerticle;
import com.vertx.web.demo.vertx_stock_broker.config.ConfigLoader;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public abstract class AbstractRestApiTest {

  protected static final Integer TEST_SERVER_PORT = 9000;
  private static final Logger LOG = LoggerFactory.getLogger(AbstractRestApiTest.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf(TEST_SERVER_PORT));
    System.setProperty(ConfigLoader.DB_HOST, "localhost");
    System.setProperty(ConfigLoader.DB_PASSWORD, "brokerdb");
    System.setProperty(ConfigLoader.DB_USER, "brokerdb");
    System.setProperty(ConfigLoader.DB_DATABASE, "brokerdb");
    System.setProperty(ConfigLoader.DB_PORT, "5432");
    LOG.warn("Using local database instance !!!");
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }
}
