package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler;

import com.vertx.web.demo.vertx_stock_broker.restapi.help.RouteHelper;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.WatchListRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model.WatchList;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    List<Map<String, Object>> parameterBatch = watchList.getAssets().stream()
      .map(asset -> {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("account_id", accountId);
        parameters.put("asset", asset.getSymbol());
        return parameters;
      }).collect(Collectors.toList());

    db.withTransaction(sqlConnection ->
      SqlTemplate.forUpdate(sqlConnection, "DELETE FROM broker.watchlist w where w.account_id = #{account_id}")
        .execute(Collections.singletonMap("account_id", accountId))
        .onFailure(RouteHelper.errorHandler(routingContext, "Failed to clear watchlist for accountId: ".concat(accountId)))
        .compose(deletionDone -> addALlForAccountId(sqlConnection, routingContext, parameterBatch))
        .onFailure(RouteHelper.errorHandler(routingContext, "Failed to update watch list for accountId: ".concat(accountId)))
        .onSuccess(result -> routingContext.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end())
    );
  }

  private Future<SqlResult<Void>> addALlForAccountId(SqlConnection client, RoutingContext routingContext, List<Map<String, Object>> parameterBatch) {
    return SqlTemplate.forUpdate(client,
        "INSERT INTO broker.watchlist VALUES(#{account_id}, #{asset}) ON CONFLICT DO NOTHING")
      .executeBatch(parameterBatch)
      .onFailure(RouteHelper.errorHandler(routingContext, "Failed to insert into watchlist"));
  }
}
