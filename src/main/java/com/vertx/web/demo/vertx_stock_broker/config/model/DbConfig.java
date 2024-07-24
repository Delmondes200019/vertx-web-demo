package com.vertx.web.demo.vertx_stock_broker.config.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {

  String host = "localhost";
  int port = 5432;
  String database = "brokerdb";
  String user = "brokerdb";
  String password = "brokerdb";

  @Override
  public String toString() {
    return "DbConfig{" +
      "host='" + host + '\'' +
      ", port=" + port +
      ", database='" + database + '\'' +
      ", user='" + user + '\'' +
      ", password='\"*****\"'" +
      '}';
  }
}
