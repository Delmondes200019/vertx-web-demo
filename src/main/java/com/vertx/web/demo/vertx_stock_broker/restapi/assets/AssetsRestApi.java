package com.vertx.web.demo.vertx_stock_broker.restapi.assets;

import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);
  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA");

  public static void attach(Router router) {
    router.get("/assets").handler(routingContext -> {
      final JsonArray response = new JsonArray();
      ASSETS.stream().map(Asset::new).forEach(response::add);
      LOG.info("Path ".concat(routingContext.normalizedPath()).concat(" responds with ").concat(response.encode()));
      routingContext.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(response.toBuffer());
    });
  }
}
