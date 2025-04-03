package com.codeplanks.taskManager.verticles;

import com.codeplanks.taskManager.utils.DbUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(
    DatabaseVerticle.class
  );
  private Pool pool;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    doDatabaseMigration()
      .compose(v -> {
        pool = DbUtils.getDbClient(vertx, config());
        return Future.succeededFuture();
      })
      .onSuccess(ar -> {
        logger.info("Database migration completed successfully.");
        startPromise.complete();
      })
      .onFailure(err -> {
        logger.error("Database migration failed!", err);
        startPromise.fail(err);
      });
  }

  Future<Void> doDatabaseMigration() {
    try {
      Flyway flyway = DbUtils.buildMigrationsConfiguration(config());
      flyway.migrate();
      return Future.<Void>succeededFuture();
    } catch (FlywayException exception) {
      logger.error("Flyway migration error", exception);
      return Future.<Void>failedFuture(exception);
    }
  }

  public Pool getPool() {
    return pool;
}

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    DbUtils.closePool();
    logger.info("Database pool closed");
    stopPromise.complete();
  }
}
