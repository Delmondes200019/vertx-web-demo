package com.vertx.web.demo.vertx_stock_broker.watchlist;

import com.vertx.web.demo.vertx_stock_broker.assets.AbstractRestApiTest;
import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model.WatchList;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.UUID;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi extends AbstractRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestApi.class);

  @Test
  void adds_and_returns_watch_list_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
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
        Assertions.assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), httpResponseAsyncResult.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
      })).compose(unused ->
        webClient.get("/account/watchlist/".concat(accountID.toString()))
          .send()
          .onComplete(testContext.succeeding(bufferHttpResponse -> {
            JsonObject jsonObject = bufferHttpResponse.bodyAsJsonObject();
            LOG.info("Response GET: ".concat(jsonObject.encode()));
            Assertions.assertEquals("{\"assets\":[{\"symbol\":\"AMZN\"},{\"symbol\":\"TSLA\"}]}", jsonObject.encode());
            Assertions.assertEquals(200, bufferHttpResponse.statusCode());
            Assertions.assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), bufferHttpResponse.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
            testContext.completeNow();
          }))
      );
  }

  @Test
  void add_and_deletes_watch_list_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
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
