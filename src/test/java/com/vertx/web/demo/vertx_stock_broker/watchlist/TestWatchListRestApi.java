package com.vertx.web.demo.vertx_stock_broker.watchlist;

import com.vertx.web.demo.vertx_stock_broker.MainVerticle;
import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model.WatchList;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.UUID;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void adds_and_returns_watch_list_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    final UUID accountID = UUID.randomUUID();

    JsonObject requestBody = new WatchList(Arrays.asList(
      new Asset("AMZN"),
      new Asset("TSLA")
    )).toJsonObject();

    webClient.put("/account/watchlist/".concat(accountID.toString()))
      .sendJsonObject(requestBody)
      .onComplete(testContext.succeeding(httpResponseAsyncResult -> {
        JsonObject jsonObject = httpResponseAsyncResult.bodyAsJsonObject();
        LOG.info("Response: ".concat(jsonObject.encode()));
        Assertions.assertEquals(requestBody.encode(), jsonObject.encode());
        Assertions.assertEquals(200, httpResponseAsyncResult.statusCode());
      })).compose(unused ->
        webClient.get("/account/watchlist/".concat(accountID.toString()))
          .send()
          .onComplete(testContext.succeeding(bufferHttpResponse -> {
            JsonObject jsonObject = bufferHttpResponse.bodyAsJsonObject();
            LOG.info("Response GET: ".concat(jsonObject.encode()));
            Assertions.assertEquals("{\"assets\":[{\"symbol\":\"AMZN\"},{\"symbol\":\"TSLA\"}]}", jsonObject.encode());
            Assertions.assertEquals(200, bufferHttpResponse.statusCode());
            testContext.completeNow();
          }))
      );
  }

  @Test
  void add_and_deletes_watch_list_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    final UUID accountID = UUID.randomUUID();

    JsonObject requestBody = new WatchList(Arrays.asList(
      new Asset("AMZN"),
      new Asset("TSLA")
    )).toJsonObject();

    webClient.put("/account/watchlist/".concat(accountID.toString()))
      .sendJsonObject(requestBody)
      .onComplete(testContext.succeeding(httpResponseAsyncResult -> {
        JsonObject jsonObject = httpResponseAsyncResult.bodyAsJsonObject();
        LOG.info("Response: ".concat(jsonObject.encode()));
        Assertions.assertEquals(requestBody.encode(), jsonObject.encode());
        Assertions.assertEquals(200, httpResponseAsyncResult.statusCode());
      })).compose(unused ->
        webClient.delete("/account/watchlist/".concat(accountID.toString()))
          .send()
          .onComplete(testContext.succeeding(bufferHttpResponse -> {
            JsonObject jsonObject = bufferHttpResponse.bodyAsJsonObject();
            LOG.info("Response DELETE: ".concat(jsonObject.encode()));
            Assertions.assertEquals("{\"assets\":[{\"symbol\":\"AMZN\"},{\"symbol\":\"TSLA\"}]}", jsonObject.encode());
            Assertions.assertEquals(200, bufferHttpResponse.statusCode());
            testContext.completeNow();
          }))
      );
  }
}
