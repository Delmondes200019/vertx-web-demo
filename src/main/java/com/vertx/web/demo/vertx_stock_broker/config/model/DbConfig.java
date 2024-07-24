package com.vertx.web.demo.vertx_stock_broker.config.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {

  String host;
  int port;
  String database;
  String user;
  String password;

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
