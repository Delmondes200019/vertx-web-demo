package com.vertx.web.demo.vertx_stock_broker.restapi.quotes.handler;

import com.vertx.web.demo.vertx_stock_broker.restapi.help.RouteHelper;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model.Quote;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.Optional;

public class GetQuoteHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);

  private final Map<String, Quote> cachedQuotes;

  public GetQuoteHandler(Map<String, Quote> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String assetParam = routingContext.pathParam("asset");
    LOG.info("Asset parameter: ".concat(assetParam));

    Optional<Quote> quote = Optional.ofNullable(cachedQuotes.get(assetParam));
    if (quote.isEmpty()) {
      RouteHelper.notFound(routingContext, "quote for asset ".concat(assetParam).concat(" not available!"));
      return;
    }

    final JsonObject response = quote.get().toJsonObject();
    LOG.info("Path ".concat(routingContext.normalizedPath()).concat(" responds with ").concat(response.encode()));
    routingContext.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(response.toBuffer());
  }
}
