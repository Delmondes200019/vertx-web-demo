package com.vertx.web.demo.vertx_stock_broker.restapi.quotes;

import com.vertx.web.demo.vertx_stock_broker.restapi.assets.AssetsRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.handler.GetQuoteHandler;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model.Quote;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  public static void attach(Router router, Pool db) {
    final Map<String, Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(asset -> cachedQuotes.put(asset, initRandomQuote(asset)));
    router.get("/quotes/:asset").handler(new GetQuoteHandler(cachedQuotes));
    router.get("/pg/quotes/:asset").handler(new GetQuoteFromDatabaseHandler(db));
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote
      .builder()
      .asset(new Asset(assetParam))
      .volume(randomValue())
      .lastPrice(randomValue())
      .bid(randomValue())
      .ask(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
