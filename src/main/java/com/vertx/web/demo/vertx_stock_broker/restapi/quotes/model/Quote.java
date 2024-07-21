package com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model.serializer.MoneySerializer;
import io.vertx.core.json.JsonObject;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quote {

  Asset asset;

  @JsonSerialize(using = MoneySerializer.class)
  BigDecimal bid;

  @JsonSerialize(using = MoneySerializer.class)
  BigDecimal ask;

  @JsonSerialize(using = MoneySerializer.class)
  BigDecimal lastPrice;

  @JsonSerialize(using = MoneySerializer.class)
  BigDecimal volume;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }

}
