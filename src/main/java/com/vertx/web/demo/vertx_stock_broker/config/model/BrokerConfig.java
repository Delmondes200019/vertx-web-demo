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
      .version(version.get())
      .build();
  }
}
