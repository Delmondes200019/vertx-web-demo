package com.vertx.web.demo.vertx_stock_broker.quotes;

import com.vertx.web.demo.vertx_stock_broker.MainVerticle;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(TestQuotesRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void return_quote_for_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    webClient.get("/quotes/AMZN")
      .send()
      .onComplete(testContext.succeeding(httpResponseAsyncResult -> {
        JsonObject jsonObject = httpResponseAsyncResult.bodyAsJsonObject();
        LOG.info("Response: ".concat(jsonObject.encode()));
        Assertions.assertEquals("{\"symbol\":\"AMZN\"}", jsonObject.getJsonObject("asset").encode());
        Assertions.assertEquals(200, httpResponseAsyncResult.statusCode());
        Assertions.assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), httpResponseAsyncResult.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        testContext.completeNow();
      }));
  }

  @Test
  void return_not_found_for_unknown_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    webClient.get("/quotes/UNKNOWN")
      .send()
      .onComplete(testContext.succeeding(httpResponseAsyncResult -> {
        JsonObject jsonObject = httpResponseAsyncResult.bodyAsJsonObject();
        LOG.info("Response: ".concat(jsonObject.encode()));
        Assertions.assertEquals("{\"message\":\"quote for asset UNKNOWN not available!\",\"path\":\"/quotes/UNKNOWN\"}",
          jsonObject.encode());
        Assertions.assertEquals(404, httpResponseAsyncResult.statusCode());
        testContext.completeNow();
      }));
  }
}
