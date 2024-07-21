package com.vertx.web.demo.vertx_stock_broker.restapi.quotes;

import com.vertx.web.demo.vertx_stock_broker.restapi.assets.AssetsRestApi;
import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model.Quote;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);

  public static void attach(Router router) {
    final Map<String, Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(asset -> cachedQuotes.put(asset, initRandomQuote(asset)));

    router.get("/quotes/:asset").handler(routingContext -> {
      final String assetParam = routingContext.pathParam("asset");
      LOG.debug("Asset parameter: ".concat(assetParam));

      Optional<Quote> quote = Optional.ofNullable(cachedQuotes.get(assetParam));
      if (quote.isEmpty()) {
        routingContext.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "quote for asset ".concat(assetParam).concat(" not available!"))
            .put("path", routingContext.normalizedPath())
            .toBuffer());
        return;
      }

      final JsonObject response = quote.get().toJsonObject();
      LOG.info("Path ".concat(routingContext.normalizedPath()).concat(" responds with ").concat(response.encode()));
      routingContext.response().end(response.toBuffer());
    });
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
