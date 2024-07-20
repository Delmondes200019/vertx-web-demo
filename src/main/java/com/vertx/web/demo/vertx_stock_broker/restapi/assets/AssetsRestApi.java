package com.vertx.web.demo.vertx_stock_broker.restapi.assets;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class AssetsRestApi {

  private static Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);

  public static void attach(Router router) {
    router.get("/assets").handler(routingContext -> {
      final JsonArray response = new JsonArray();
      response
        .add(new JsonObject().put("symnbol", "AAPL"))
        .add(new JsonObject().put("symnbol", "AMZN"))
        .add(new JsonObject().put("symnbol", "NFLX"))
        .add(new JsonObject().put("symnbol", "TSLA"));

      LOG.info("Path ".concat(routingContext.normalizedPath()).concat(" responds with ").concat(response.encode()));
      routingContext.response().end(response.toBuffer());
    });
  }
}
