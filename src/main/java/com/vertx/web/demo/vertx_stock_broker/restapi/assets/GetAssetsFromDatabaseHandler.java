package com.vertx.web.demo.vertx_stock_broker.restapi.assets;

import com.vertx.web.demo.vertx_stock_broker.restapi.help.RouteHelper;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;

public class GetAssetsFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsFromDatabaseHandler.class);

  private final PgPool db;

  public GetAssetsFromDatabaseHandler(PgPool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    db.query("SELECT  a.value FROM broker.assets a")
      .execute()
      .onFailure(RouteHelper.errorHandler(routingContext, "failed to get assets from database"))
      .onSuccess(rows -> {
        JsonArray response = new JsonArray();
        rows.forEach(row -> response.add(row.getValue("value")));
        LOG.info(Thread.currentThread().getName().concat(": Path ".concat(routingContext.normalizedPath())
          .concat(" responds with ").concat(response.encode())));
        routingContext.response().end(response.toBuffer());
      });
  }
}
