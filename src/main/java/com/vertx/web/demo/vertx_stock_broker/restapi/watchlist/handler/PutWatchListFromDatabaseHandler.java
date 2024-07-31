package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler;

import com.vertx.web.demo.vertx_stock_broker.restapi.help.RouteHelper;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.WatchListRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model.WatchList;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;
import java.util.Map;

public class PutWatchListFromDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  public PutWatchListFromDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String accountId = WatchListRestApi.getAccountId(routingContext);

    JsonObject body = routingContext.body().asJsonObject();
    WatchList watchList = body.mapTo(WatchList.class);


    watchList.getAssets().forEach(asset -> {
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("account_id", accountId);
      parameters.put("asset", asset.getSymbol());

      SqlTemplate.forUpdate(db,
          "INSERT INTO broker.watchlist VALUES(#{account_id}, #{asset})")
        .execute(parameters)
        .onFailure(RouteHelper.errorHandler(routingContext, "Failed to insert into watchlist"))
        .onSuccess(result -> {
          if (!routingContext.response().ended()) {
            routingContext.response()
              .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
              .end();
          }
        });
    });
  }
}
