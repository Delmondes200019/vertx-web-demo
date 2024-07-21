package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler;

import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.WatchListRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model.WatchList;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(DeleteWatchListHandler.class);

  private final Map<UUID, WatchList> watchListPerAccount;

  public DeleteWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String accountId = WatchListRestApi.getAccountId(routingContext);
    LOG.info(routingContext.normalizedPath().concat(" for account ").concat(accountId));
    WatchList deleted = watchListPerAccount.remove(UUID.fromString(accountId));
    LOG.info("Deleted ".concat(deleted.toJsonObject().encode()).concat(", remaining: ")
      .concat(watchListPerAccount.values().toString()));
    routingContext.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(deleted.toJsonObject().toBuffer());
  }
}
