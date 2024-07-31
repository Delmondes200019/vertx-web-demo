package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler;

import com.vertx.web.demo.vertx_stock_broker.restapi.help.RouteHelper;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.WatchListRestApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;

public class GetWatchListFromDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  public GetWatchListFromDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String accountId = WatchListRestApi.getAccountId(routingContext);
    SqlTemplate.forQuery(db, "SELECT w.asset FROM broker.watchlist w where w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(RouteHelper.errorHandler(routingContext, "Failed to fetch watchlist for account id: ".concat(accountId)))
      .onSuccess(assets -> {
        if (!assets.iterator().hasNext()) {
          RouteHelper.notFound(routingContext, "watchlist for account ".concat(accountId).concat(" is not available!"));
          return;
        }
        JsonArray response = new JsonArray();
        assets.forEach(response::add);
        LOG.info("Path ".concat(routingContext.normalizedPath()).concat(" responds with ").concat(response.encode()));
        routingContext.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
