package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler;

import com.vertx.web.demo.vertx_stock_broker.restapi.help.RouteHelper;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.WatchListRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;

public class DeleteWatchListDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(DeleteWatchListDatabaseHandler.class);

  private final Pool db;

  public DeleteWatchListDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String accountId = WatchListRestApi.getAccountId(routingContext);

    SqlTemplate.forUpdate(db, "DELETE FROM broker.watchlist where account_id=#{account_id}")
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(RouteHelper.errorHandler(routingContext, "Failed to delete watchlist for accountid: ".concat(accountId)))
      .onSuccess(result -> {
        LOG.info("Deleted ".concat(String.valueOf(result.rowCount())).concat(" rows for account id: ")
          .concat(accountId));
        routingContext.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });


  }
}
