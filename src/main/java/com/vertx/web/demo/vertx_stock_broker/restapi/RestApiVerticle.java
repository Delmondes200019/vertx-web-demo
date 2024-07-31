package com.vertx.web.demo.vertx_stock_broker.restapi;

import com.vertx.web.demo.vertx_stock_broker.config.ConfigLoader;
import com.vertx.web.demo.vertx_stock_broker.config.model.BrokerConfig;
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
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.impl.PgPoolOptions;
import io.vertx.sqlclient.PoolOptions;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        LOG.info("Retrieved configuration: ".concat(configuration.toString()));
        startHttpServerAndAttachRoutes(startPromise, configuration);
      });
  }

  private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, BrokerConfig configuration) {
    final PgPool db = createDbPool(configuration);

    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(RestApiVerticle::handleFailure);

    AssetsRestApi.attach(restApi, db);
    QuotesRestApi.attach(restApi, db);
    WatchListRestApi.attach(restApi, db);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(throwable -> LOG.error("Http server error: ", throwable))
      .listen(configuration.getServerPort()).onComplete(http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info(Thread.currentThread().getName().concat(": HTTP server started on port "
            .concat(configuration.getServerPort().toString())));
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private PgPool createDbPool(BrokerConfig configuration) {
    final PgConnectOptions connectOptions = new PgConnectOptions()
      .setHost(configuration.getDbConfig().getHost())
      .setDatabase(configuration.getDbConfig().getDatabase())
      .setPort(configuration.getDbConfig().getPort())
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword());

    final PoolOptions poolOptions = new PgPoolOptions().setMaxSize(4);

    return PgPool.pool(vertx, connectOptions, poolOptions);
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
