package com.vertx.web.demo.vertx_stock_broker.restapi.assets;

import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;

public class AssetsRestApi {

  private static Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);

  public static void attach(Router router) {
    router.get("/assets").handler(routingContext -> {
      final JsonArray response = new JsonArray();
      response
        .add(new Asset("AAPL"))
        .add(new Asset("AMZN"))
        .add(new Asset( "NFLX"))
        .add(new Asset("TSLA"));

      LOG.info("Path ".concat(routingContext.normalizedPath()).concat(" responds with ").concat(response.encode()));
      routingContext.response().end(response.toBuffer());
    });
  }
}
