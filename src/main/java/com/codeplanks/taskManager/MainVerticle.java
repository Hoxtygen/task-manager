package com.codeplanks.taskManager;

import com.codeplanks.taskManager.controller.TaskController;
import com.codeplanks.taskManager.repository.TaskRepository;
import com.codeplanks.taskManager.router.TaskRouter;
import com.codeplanks.taskManager.service.TaskService;
import com.codeplanks.taskManager.verticles.ApiVerticle;
import com.codeplanks.taskManager.verticles.DatabaseVerticle;
import com.codeplanks.taskManager.verticles.TaskVerticle;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(
    MainVerticle.class
  );
  final JsonObject loadedConfig = new JsonObject();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    DatabindCodec.mapper().registerModule(new JavaTimeModule());
    DatabindCodec
      .mapper()
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    DatabindCodec.prettyMapper().registerModule(new JavaTimeModule());
    DatabindCodec
      .prettyMapper()
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    doConfig()
      .compose(this::storeConfig)
      .compose(this::deployDatabaseVerticle)
      .compose(this::deployOtherVerticles)
      .onComplete(startPromise::handle);
  }

  public static void main(String[] args) {
    System.setProperty(
      "vertx.logger-delegate-factory-class-name",
      "io.vertex.core.logging.SLF4JLogDelegateFactory"
    );

    PrometheusMeterRegistry registry = new PrometheusMeterRegistry(
      PrometheusConfig.DEFAULT
    );
    new UptimeMetrics().bindTo(registry);

    final Vertx vertx = Vertx.vertx(
      new VertxOptions()
        .setMetricsOptions(
          new MicrometerMetricsOptions()
            .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
            .setJvmMetricsEnabled(true)
            .setEnabled(true)
        )
    );

    vertx
      .deployVerticle(new MainVerticle())
      .onFailure(throwable -> System.exit(-1));
  }

  private Future<Pool> deployDatabaseVerticle(Void unused) {
    DeploymentOptions deploymentOptions = new DeploymentOptions()
      .setConfig(loadedConfig);
    Promise<Pool> poolPromise = Promise.promise();

    DatabaseVerticle databaseVerticle = new DatabaseVerticle();

    vertx.deployVerticle(
      databaseVerticle,
      deploymentOptions,
      res -> {
        if (res.succeeded()) {
          logger.info("Database verticle deployed successfully");
          poolPromise.complete(databaseVerticle.getPool());
        } else {
          logger.error("Failed to deploy database verticle", res.cause());
        }
      }
    );
    return poolPromise.future();
  }

  /**
   * Store loaded configuration for use in subsequent operations
   * @param config The configuration loaded via Vert.x Config
   * @return A {@link Future} of type {@link Void} indication the success or failure of this operation
   */
  Future<Void> storeConfig(JsonObject config) {
    loadedConfig.mergeIn(config);
    return Future.<Void>succeededFuture();
  }

  Future<JsonObject> doConfig() {
    ConfigStoreOptions defaultStoreOption = new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path", "config.json"));

    ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
      .setType("json")
      .setConfig(config());

    ConfigRetrieverOptions retrieverOptions = new ConfigRetrieverOptions()
      .addStore(defaultStoreOption)
      .addStore(configStoreOptions);

    ConfigRetriever configRetriever = ConfigRetriever.create(
      vertx,
      retrieverOptions
    );

    return Future.future(promise -> configRetriever.getConfig(promise));
  }

  private Future<Void> deployOtherVerticles(Pool dbPool) {
    DeploymentOptions options = new DeploymentOptions().setConfig(loadedConfig);

    TaskRepository taskRepository = new TaskRepository();
    TaskService taskService = new TaskService(dbPool, taskRepository);
    TaskController taskController = new TaskController(taskService);
    TaskRouter taskRouter = new TaskRouter(vertx, taskController);

    // Pass the pool to the other verticles via constructor injection
    TaskVerticle taskVerticle = new TaskVerticle(dbPool, taskRepository);
    ApiVerticle apiVerticle = new ApiVerticle(dbPool, taskRouter);

    return deployVerticle(taskVerticle, options)
      .compose(id -> deployVerticle(apiVerticle, options))
      .onSuccess(v -> logger.info("All verticles deployed successfully"))
      .onFailure(err -> logger.error("Error deploying verticles", err))
      .mapEmpty();
  }

  private Future<String> deployVerticle(
    AbstractVerticle verticle,
    DeploymentOptions options
  ) {
    Promise<String> promise = Promise.promise();
    vertx.deployVerticle(verticle, options, promise);
    return promise.future();
  }
}
