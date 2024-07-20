package com.vertx.web.demo.vertx_stock_broker.restapi.assets.model;

public class Asset {

  private final String symbol;

  public Asset(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }
}
