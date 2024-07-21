package com.vertx.web.demo.vertx_stock_broker.restapi.assets.handler;

import com.vertx.web.demo.vertx_stock_broker.restapi.assets.AssetsRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

public class GetAssetsHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(RoutingContext routingContext) {
    final JsonArray response = new JsonArray();
    AssetsRestApi.ASSETS.stream().map(Asset::new).forEach(response::add);
    LOG.info("Path ".concat(routingContext.normalizedPath()).concat(" responds with ").concat(response.encode()));
    routingContext.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(response.toBuffer());
  }
}
