package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler;

import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.WatchListRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model.WatchList;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GetWatchListHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetWatchListHandler.class);

  private final Map<UUID, WatchList> watchListPerAccount;

  public GetWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String accountId = WatchListRestApi.getAccountId(routingContext);
    LOG.info(routingContext.normalizedPath().concat(" for account ").concat(accountId));
    Optional<WatchList> watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));

    if (watchList.isEmpty()) {
      routingContext.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message", "watchlist for account ".concat(accountId).concat(" not available!"))
          .put("path", routingContext.normalizedPath())
          .toBuffer());
      return;
    }

    routingContext.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(watchList.get().toJsonObject().toBuffer());
  }
}
