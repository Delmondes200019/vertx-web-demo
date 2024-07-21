package com.vertx.web.demo.vertx_stock_broker.restapi.assets;

import com.vertx.web.demo.vertx_stock_broker.restapi.assets.handler.GetAssetsHandler;
import io.vertx.ext.web.Router;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA");

  public static void attach(Router router) {
    router.get("/assets").handler(new GetAssetsHandler());
  }
}
