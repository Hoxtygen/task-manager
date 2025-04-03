package com.codeplanks.taskManager.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

public class DbUtils {

  private static Pool pool;

  private DbUtils() {}

  public static synchronized Pool getDbClient(Vertx vertx, JsonObject config) {
    if (pool == null) {
      JsonObject dataSourceConfig = config.getJsonObject("db");
      if (dataSourceConfig == null) {
        throw new IllegalArgumentException(
          "Datasource configuration not found"
        );
      }

      PgConnectOptions connectOptions = new PgConnectOptions()
        .setPort(dataSourceConfig.getInteger("port", 5432))
        .setHost(dataSourceConfig.getString("host", "localhost"))
        .setDatabase(dataSourceConfig.getString("database", "taskmanager"))
        .setUser(dataSourceConfig.getString("username", "postgres"))
        .setPassword(dataSourceConfig.getString("password", "postgres"));

      PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
      pool = Pool.pool(vertx, connectOptions, poolOptions);
    }
    return pool;
  }

  public static Flyway buildMigrationsConfiguration(JsonObject config) {
    JsonObject datasourceConfig = config.getJsonObject("db");

    if (datasourceConfig == null) {
      throw new IllegalArgumentException("Datasource configuration not found");
    }

    String url =
      "jdbc:postgresql://" +
      datasourceConfig.getString("host", "localhost") +
      ":" +
      datasourceConfig.getInteger("port", 5432) +
      "/" +
      datasourceConfig.getString("database", "taskmanager");

    return new FluentConfiguration()
      .dataSource(
        url,
        datasourceConfig.getString("username", "postgres"),
        datasourceConfig.getString("password", "postgres")
      )
      .load();
  }

  public static void closePool() {
    if (pool != null) {
      pool.close();
      pool = null; // Reset to null so it can be recreated if needed
    }
  }
}
