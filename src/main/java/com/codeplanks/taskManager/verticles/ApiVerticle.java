package com.codeplanks.taskManager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class ApiVerticle extends AbstractVerticle {

  public void start(Promise<Void> startPromise) {
    final Router router = Router.router(vertx);
    buildHttpServer(vertx, startPromise, router);
  }

  private void buildHttpServer(
    Vertx vertx,
    Promise<Void> promise,
    Router router
  ) {
    final int port = 8889;
    vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(
        port,
        http -> {
          if (http.succeeded()) {
            promise.complete();
          } else {
            promise.fail(http.cause());
          }
        }
      );
  }
}
