package com.vertx.web.demo.vertx_stock_broker.assets;

import com.sun.tools.javac.Main;
import com.vertx.web.demo.vertx_stock_broker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestAssetsRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(TestAssetsRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void return_all_assets(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    webClient.get("/assets")
      .send()
      .onComplete(testContext.succeeding(httpResponseAsyncResult -> {
        JsonArray jsonArray = httpResponseAsyncResult.bodyAsJsonArray();
        LOG.info("Response: ".concat(jsonArray.encode()));
        Assertions.assertEquals("[{\"symbol\":\"AAPL\"},{\"symbol\":\"AMZN\"},{\"symbol\":\"FB\"},{\"symbol\":\"GOOG\"},{\"symbol\":\"MSFT\"},{\"symbol\":\"NFLX\"},{\"symbol\":\"TSLA\"}]",
          jsonArray.encode());
        Assertions.assertEquals(200, httpResponseAsyncResult.statusCode());
        testContext.completeNow();
      }));
  }
}
