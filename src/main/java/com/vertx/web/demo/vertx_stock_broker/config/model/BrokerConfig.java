package com.vertx.web.demo.vertx_stock_broker.config.model;

import com.vertx.web.demo.vertx_stock_broker.config.ConfigLoader;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
@ToString
public class BrokerConfig {

  Integer serverPort;
  String version;
  DbConfig dbConfig;

  public static BrokerConfig from(final JsonObject config) {
    Optional<Integer> serverPort = Optional.ofNullable(config.getInteger(ConfigLoader.SERVER_PORT));
    if (serverPort.isEmpty()) {
      throw new RuntimeException(ConfigLoader.SERVER_PORT.concat(" not configured!"));
    }
    Optional<String> version = Optional.ofNullable(config.getString("version"));
    if(version.isEmpty()){
      throw new RuntimeException("version is not configured in config file");
    }

    return BrokerConfig.builder()
      .serverPort(serverPort.get())
      .dbConfig(parseDbConfig(config))
      .version(version.get())
      .build();
  }

  private static DbConfig parseDbConfig(JsonObject config) {
    return DbConfig.builder()
      .database(config.getString(ConfigLoader.DB_DATABASE))
      .port(config.getInteger(ConfigLoader.DB_PORT))
      .password(config.getString(ConfigLoader.DB_PASSWORD))
      .host(config.getString(ConfigLoader.DB_HOST))
      .user(config.getString(ConfigLoader.DB_USER))
      .build();
  }
}
