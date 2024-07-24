package com.vertx.web.demo.vertx_stock_broker.config;

import com.vertx.web.demo.vertx_stock_broker.config.model.BrokerConfig;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class ConfigLoader {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
  public static final String SERVER_PORT = "SERVER_PORT";
  public static final String DB_HOST = "DB_HOST";
  public static final String DB_PORT = "DB_PORT";
  public static final String DB_DATABASE = "DB_DATABASE";
  public static final String DB_USER = "DB_USER";
  public static final String DB_PASSWORD = "DB_PASSWORD";
  private static final List<String> EXPOSED_ENV_VARIABLES = List.of(
    SERVER_PORT, DB_HOST, DB_PASSWORD, DB_USER, DB_PORT, DB_DATABASE);
  private static final String CONFIG_FILE = "application.yml";

  public static Future<BrokerConfig> load(Vertx vertx) {
    final JsonArray exposedKeys = new JsonArray();
    EXPOSED_ENV_VARIABLES.forEach(exposedKeys::add);
    LOG.info("Fetch configuration for ".concat(exposedKeys.encode()));

    ConfigStoreOptions configStoreOptionsEnv = new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("keys", exposedKeys));

    ConfigStoreOptions configStoreOptionsSys = new ConfigStoreOptions()
      .setType("sys")
      .setConfig(new JsonObject().put("cache", false));

    ConfigStoreOptions configStoreOptionsYaml = new ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      .setConfig(new JsonObject().put("path", CONFIG_FILE));

    ConfigRetriever configRetriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
      .addStore(configStoreOptionsYaml) //less priority over the other config stores
      .addStore(configStoreOptionsSys)
      .addStore(configStoreOptionsEnv)); //high priority over the other config stores

    return configRetriever.getConfig().map(BrokerConfig::from);
  }

}
