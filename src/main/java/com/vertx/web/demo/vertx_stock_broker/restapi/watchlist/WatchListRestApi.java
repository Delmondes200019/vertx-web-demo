package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist;

import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler.DeleteWatchListHandler;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler.GetWatchListHandler;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.handler.PutWatchListHandler;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model.WatchList;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WatchListRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  public static void attach(Router router) {
    final Map<UUID, WatchList> watchListPerAccount = new HashMap<>();

    final String path = "/account/watchlist/:accountId";

    router.get(path).handler(new GetWatchListHandler(watchListPerAccount));
    router.put(path).handler(new PutWatchListHandler(watchListPerAccount));
    router.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));
  }

  public static String getAccountId(RoutingContext routingContext) {
    return routingContext.pathParam("accountId");
  }
}
