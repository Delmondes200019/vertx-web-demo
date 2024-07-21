package com.vertx.web.demo.vertx_stock_broker.assets;

import com.sun.tools.javac.Main;
import com.vertx.web.demo.vertx_stock_broker.MainVerticle;
import com.vertx.web.demo.vertx_stock_broker.config.ConfigLoader;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
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
public class TestAssetsRestApi extends AbstractRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(TestAssetsRestApi.class);

  @Test
  void return_all_assets(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions()
      .setDefaultPort(TEST_SERVER_PORT));
    webClient.get("/assets")
      .send()
      .onComplete(testContext.succeeding(httpResponseAsyncResult -> {
        JsonArray jsonArray = httpResponseAsyncResult.bodyAsJsonArray();
        LOG.info("Response: ".concat(jsonArray.encode()));
        Assertions.assertEquals("[{\"symbol\":\"AAPL\"},{\"symbol\":\"AMZN\"},{\"symbol\":\"FB\"},{\"symbol\":\"GOOG\"},{\"symbol\":\"MSFT\"},{\"symbol\":\"NFLX\"},{\"symbol\":\"TSLA\"}]",
          jsonArray.encode());
        Assertions.assertEquals(200, httpResponseAsyncResult.statusCode());
        Assertions.assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), httpResponseAsyncResult.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        testContext.completeNow();
      }));
  }
}
