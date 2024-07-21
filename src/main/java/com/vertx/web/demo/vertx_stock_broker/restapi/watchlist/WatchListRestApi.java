package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist;

import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model.WatchList;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WatchListRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  public static void attach(Router router) {
    final Map<UUID, WatchList> watchListPerAccount = new HashMap<>();

    final String path = "/account/watchlist/:accountId";

    router.get(path).handler(routingContext -> {
      final String accountId = routingContext.pathParam("accountId");
      LOG.debug(routingContext.normalizedPath().concat(" for account ").concat(accountId));
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

      routingContext.response().end(watchList.get().toJsonObject().toBuffer());
    });

    router.put(path).handler(routingContext -> {
      final String accountId = routingContext.pathParam("accountId");
      LOG.debug(routingContext.normalizedPath().concat(" for account ").concat(accountId));

      JsonObject bodyAsJson = routingContext.body().asJsonObject();
      WatchList watchList = bodyAsJson.mapTo(WatchList.class);
      watchListPerAccount.put(UUID.fromString(accountId), watchList);

      routingContext.response().end(bodyAsJson.toBuffer());
    });
    router.delete(path).handler(routingContext -> {

    });
  }
}
