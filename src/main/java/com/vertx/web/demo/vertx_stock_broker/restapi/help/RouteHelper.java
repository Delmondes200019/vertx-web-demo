package com.vertx.web.demo.vertx_stock_broker.restapi.help;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class RouteHelper {

  private static final Logger LOG = LoggerFactory.getLogger(RouteHelper.class);

  public static Handler<Throwable> errorHandler(RoutingContext routingContext, String message) {
    return throwable -> {
      LOG.error("Failure: ", throwable);
      routingContext.response()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .end(new JsonObject()
          .put("message", message)
          .put("path", routingContext.normalizedPath())
          .toBuffer());
    };
  }

  public static void notFound(RoutingContext routingContext, String message) {
    routingContext.response()
      .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
      .end(new JsonObject()
        .put("message", message)
        .put("path", routingContext.normalizedPath())
        .toBuffer());
  }
}
