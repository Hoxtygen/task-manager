package com.codeplanks.taskManager;

import com.codeplanks.taskManager.verticles.ApiVerticle;
import com.codeplanks.taskManager.verticles.DatabaseVerticle;
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
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;

public class MainVerticle extends AbstractVerticle {

  final JsonObject loadedConfig = new JsonObject();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    doConfig().compose(this::storeConfig).compose(this::deployOtherVerticles).onComplete(startPromise::handle);
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
      .deployVerticle(MainVerticle.class.getName())
      .onFailure(throwable -> System.exit(-1));
  }

  Future<Void> deployOtherVerticles(Void unused) {
    DeploymentOptions opts = new DeploymentOptions().setConfig(loadedConfig);

    Future<String> databaseVerticle = Future.future(promise ->
      vertx.deployVerticle(new DatabaseVerticle(), opts, promise)
    );
    Future<String> apiVerticle = Future.future(promise ->
      vertx.deployVerticle(new ApiVerticle(), opts, promise)
    );

    return Future.all(databaseVerticle, apiVerticle).mapEmpty();
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
}
