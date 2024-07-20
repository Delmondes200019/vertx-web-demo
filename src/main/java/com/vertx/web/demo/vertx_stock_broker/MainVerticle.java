package com.vertx.web.demo.vertx_stock_broker;

import com.vertx.web.demo.vertx_stock_broker.restapi.assets.AssetsRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  private static Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(throwable -> LOG.error("Unhandled: {}", throwable.getCause()));
    vertx.deployVerticle(new MainVerticle(), stringAsyncResult -> {
      if (stringAsyncResult.failed()) {
        LOG.error("Failed to deploy: {}", stringAsyncResult.cause());
        return;
      }
      LOG.info("Deployed ".concat(MainVerticle.class.getName()));
    });
  }

  public void start(Promise<Void> startPromise) throws Exception {
    final Router restApi = Router.router(vertx);
    restApi.route().failureHandler(MainVerticle::handleFailure);

    AssetsRestApi.attach(restApi);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(throwable -> LOG.error("Http server error: ", throwable))
      .listen(8888).onComplete(http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port 8888");
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
