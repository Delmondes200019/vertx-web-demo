package com.vertx.web.demo.vertx_stock_broker.restapi;

import com.vertx.web.demo.vertx_stock_broker.MainVerticle;
import com.vertx.web.demo.vertx_stock_broker.restapi.assets.AssetsRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.QuotesRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startHttpServerAndAttachRoutes(startPromise);
  }

  private void startHttpServerAndAttachRoutes(Promise<Void> startPromise) {
    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(RestApiVerticle::handleFailure);

    AssetsRestApi.attach(restApi);
    QuotesRestApi.attach(restApi);
    WatchListRestApi.attach(restApi);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(throwable -> LOG.error("Http server error: ", throwable))
      .listen(MainVerticle.PORT).onComplete(http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info(Thread.currentThread().getName().concat(": HTTP server started on port 8888 "));
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private static void handleFailure(RoutingContext errorContext) {
    if (errorContext.response().ended()) {
      //Ignore completed response
      return;
    }
    LOG.error("Router error: ", errorContext.failure());
    errorContext.response()
      .setStatusCode(500)
      .end(new JsonObject().put("message", "Something went wrong").toBuffer());
  }

}
