package com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model.serializer.MoneySerializer;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteEntity {

  String asset;

  @JsonSerialize(using = MoneySerializer.class)
  BigDecimal bid;

  @JsonSerialize(using = MoneySerializer.class)
  BigDecimal ask;

  @JsonProperty("last_price")
  @JsonSerialize(using = MoneySerializer.class)
  BigDecimal lastPrice;

  @JsonSerialize(using = MoneySerializer.class)
  BigDecimal volume;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
