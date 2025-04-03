package com.codeplanks.taskManager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeplanks.taskManager.router.TaskRouter;

public class ApiVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(
    ApiVerticle.class
  );
  private final Pool dbPool;
  private final TaskRouter taskRouter;

  public ApiVerticle(Pool dbPool, TaskRouter taskRouter) {
    this.dbPool = dbPool;
    this.taskRouter = taskRouter;
  }

  public void start(Promise<Void> startPromise) {
    Router mainRouter = Router.router(vertx);

    // Enable body handling and logging for all routes
    mainRouter
    .route("/api*")
    .handler(LoggerHandler.create(LoggerFormat.DEFAULT));

    mainRouter.route("/api*").handler(BodyHandler.create());
   
    mainRouter
      .get("/api")
      .handler(routingContext -> {
        JsonObject response = new JsonObject()
          .put("status", 200)
          .put("message", "Welcome to the base API!")
          .put("data", "Hello world");

        routingContext
          .response()
          .putHeader("Content-Type", "application/json")
          .end(response.encodePrettily());
      });

    mainRouter
      .get("/api/v1")
      .handler(routingContext -> {
        JsonObject response = new JsonObject()
          .put("status", 200)
          .put("message", "Welcome to API version 1!")
          .put("data", "Version 1 is here");

        routingContext
          .response()
          .putHeader("Content-Type", "application/json")
          .end(response.encodePrettily());
      });

    if (taskRouter != null) {
      taskRouter.setRouter(mainRouter);

      vertx
        .createHttpServer()
        .requestHandler(mainRouter)
        .listen(8889)
        .onSuccess(http -> {
          logger.info("Http server started on port 8889");
          startPromise.complete();
        })
        .onFailure(startPromise::fail);
    } else {
      logger.error("TaskVerticle not found");
      startPromise.fail("TaskVerticle not found");
    }
  }
}
