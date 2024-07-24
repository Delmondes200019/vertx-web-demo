package com.vertx.web.demo.vertx_stock_broker.config.migration;

import com.vertx.web.demo.vertx_stock_broker.config.model.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {

  private static final Logger LOG = LoggerFactory.getLogger(FlywayMigration.class);

  public static Future<Void> migrate(Vertx vertx, DbConfig config) {
    return vertx.executeBlocking(promise -> {
      //Flyway migration is blocking => uses JDBC
      execute(config);
      promise.complete();
    });
  }

  private static void execute(DbConfig config) {
    String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
      config.getHost(),
      config.getPort(),
      config.getDatabase());

    LOG.info("Migrating database schema using jdbc url: ".concat(jdbcUrl));

    Flyway flyway = Flyway.configure()
      .dataSource(jdbcUrl, config.getUser(), config.getPassword())
      .schemas("broker")
      .defaultSchema("broker")
      .load();

    Optional<MigrationInfo> current = Optional.ofNullable(flyway.info().current());
    current.ifPresent(migrationInfo ->
      Optional.ofNullable(migrationInfo.getVersion()).ifPresent(version ->
        LOG.info("db schema is at version: ".concat(migrationInfo.getVersion()
          .getVersion()))
      )
    );

    MigrationInfo[] pendingMigrations = flyway.info().pending();
    LOG.info("Pending migrations are: ".concat(printPendingMigrations(pendingMigrations)));

    flyway.migrate();
  }

  private static String printPendingMigrations(MigrationInfo[] pendingMigrations) {
    if (Objects.isNull(pendingMigrations)) {
      return "[]";
    }
    return Arrays.stream(pendingMigrations)
      .map(migrationInfo -> migrationInfo.getVersion().getVersion().concat(" - ").concat(migrationInfo.getDescription()))
      .collect(Collectors.joining(",", "[", "]"));
  }
}
