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
  private static final List<String> EXPOSED_ENV_VARIABLES = List.of(SERVER_PORT);

  public static Future<BrokerConfig> load(Vertx vertx) {
    final JsonArray exposedKeys = new JsonArray();
    EXPOSED_ENV_VARIABLES.forEach(exposedKeys::add);
    LOG.info("Fetch configuration for ".concat(exposedKeys.encode()));

    ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("keys", exposedKeys));

    ConfigRetriever configRetriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
      .addStore(configStoreOptions));

    return configRetriever.getConfig().map(BrokerConfig::from);
  }

}
