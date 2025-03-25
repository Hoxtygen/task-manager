package com.codeplanks.taskManager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

public class DatabaseVerticle extends AbstractVerticle {

  private Pool pool;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    doDatabaseMigration()
      .compose(this::configureSqlClient)
      .onComplete(ar -> {
        if (ar.succeeded()) {
          startPromise.complete();
        } else {
          startPromise.fail(ar.cause());
        }
      });
  }

  Future<Void> configureSqlClient(Void unused) {

    PgConnectOptions pgConnectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("taskManager")
      .setUser("postgres")
      .setPassword("postgres");

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    pool =
      PgBuilder
        .pool()
        .with(poolOptions)
        .connectingTo(pgConnectOptions)
        .using(vertx)
        .build();

    return Future.succeededFuture();
  }

  Future<Void> doDatabaseMigration() {
    JsonObject dbConfig = config().getJsonObject("db", new JsonObject());
    String url = dbConfig.getString(
      "url",
      "jdbc:postgresql://127.0.0.1:5432/taskmanager"
    );

    String adminUser = dbConfig.getString("admin_user", "postgres");
    String adminPass = dbConfig.getString("admin_pass", "postres");

    Flyway flyway = Flyway
      .configure()
      .dataSource(url, adminUser, adminPass)
      .load();

    try {
      flyway.migrate();
      return Future.<Void>succeededFuture();
    } catch (FlywayException exception) {
      return Future.<Void>failedFuture(exception);
    }
  }
}
