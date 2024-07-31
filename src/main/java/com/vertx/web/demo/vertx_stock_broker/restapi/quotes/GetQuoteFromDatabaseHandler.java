package com.vertx.web.demo.vertx_stock_broker.restapi.quotes;

import com.vertx.web.demo.vertx_stock_broker.restapi.help.RouteHelper;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model.QuoteEntity;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;

public class GetQuoteFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteFromDatabaseHandler.class);

  private final Pool db;

  public GetQuoteFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String assetParam = routingContext.pathParam("asset");
    LOG.info("Asset parameter: ".concat(assetParam));

    SqlTemplate.forQuery(db, "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume FROM broker.quotes q where asset=#{asset}")
      .mapTo(QuoteEntity.class)
      .execute(Collections.singletonMap("asset", assetParam))
      .onFailure(RouteHelper.errorHandler(routingContext,
        "failed to get quotes for asset ".concat(assetParam).concat(" from database")))
      .onSuccess(quotes -> {
        if (!quotes.iterator().hasNext()) {
          RouteHelper.notFound(routingContext, "quote for asset ".concat(assetParam).concat(" not available!"));
          return;
        }
        JsonObject response = quotes.iterator().next().toJsonObject();
        LOG.info("Path ".concat(routingContext.normalizedPath()).concat(" responds with ").concat(response.encode()));
        routingContext.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });

  }
}
